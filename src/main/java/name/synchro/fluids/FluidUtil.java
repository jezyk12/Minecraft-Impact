package name.synchro.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public final class FluidUtil {
    private static final PalettedContainer.PaletteProvider FLUID_STATE_PALETTE_PROVIDER
            = PalettedContainer.PaletteProvider.BLOCK_STATE;
    public static final Codec<PalettedContainer<FluidState>> CODEC
            = PalettedContainer.createPalettedContainerCodec(Fluid.STATE_IDS, FluidState.CODEC, FLUID_STATE_PALETTE_PROVIDER, Fluids.EMPTY.getDefaultState());
    private static final Logger LOGGER = LoggerFactory.getLogger(FluidUtil.class);
    public static final String KEY_FLUID_STATES = "fluid_states";

    public static PalettedContainer<FluidState> createFluidStatePaletteContainer() {
        return new PalettedContainer<>(Fluid.STATE_IDS, Fluids.EMPTY.getDefaultState(), FLUID_STATE_PALETTE_PROVIDER);
    }

    public static int getRawIdFromState(FluidState state) {
        if (state == null) {
            return 0;
        }
        else {
            int i = Fluid.STATE_IDS.getRawId(state);
            return i == -1 ? 0 : i;
        }
    }

    public static PalettedContainer<FluidState> deserialize(NbtCompound nbtCompound, ChunkPos chunkPos, int yPos) {
        DataResult<PalettedContainer<FluidState>> dataResult;
        PalettedContainer<FluidState> palettedContainer;
        if (nbtCompound.contains(KEY_FLUID_STATES, NbtElement.COMPOUND_TYPE)) {
            dataResult = CODEC.parse(NbtOps.INSTANCE, nbtCompound.getCompound(KEY_FLUID_STATES))
                    .promotePartial((errorMessage) -> logRecoverableError(chunkPos, yPos, errorMessage));
            palettedContainer = dataResult.getOrThrow(false, LOGGER::error);
        } else {
            palettedContainer = createFluidStatePaletteContainer();
        }
        return palettedContainer;
    }

    public static void serialize(NbtCompound nbt, ChunkSection chunkSection) {
        DataResult<NbtElement> dataResult = CODEC.encodeStart(NbtOps.INSTANCE, ((FluidHelper.ForChunkSection) chunkSection).getFluidStateContainer());
        nbt.put(KEY_FLUID_STATES, dataResult.getOrThrow(false, LOGGER::error));
    }

    private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
        LOGGER.error("Recoverable errors when loading fluid states for section [{}, {}, {}]: {}", chunkPos.x, y, chunkPos.z, message);
    }

    public static int countFluidStates(PalettedContainer<FluidState> container) {
        class FluidStateCounter implements PalettedContainer.Counter<FluidState> {
            protected int nonEmptyFluidWithRandomTickCount = 0;

            @Override
            public void accept(FluidState state, int count) {
                if (!state.isEmpty() && state.hasRandomTicks()) {
                    this.nonEmptyFluidWithRandomTickCount += count;
                }
            }
        }
        FluidStateCounter counter = new FluidStateCounter();
        container.count(counter);
        return counter.nonEmptyFluidWithRandomTickCount;
    }

    public static FluidState getFluidStateInRenderedChunk(List<PalettedContainer<FluidState>> fluidStateContainers, BlockPos pos, Chunk chunk) {
        if (fluidStateContainers == null) {
            return Fluids.EMPTY.getDefaultState();
        } else {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            try {

                int index = chunk.getSectionIndex(y);
                if (index >= 0 && index < fluidStateContainers.size()) {
                    PalettedContainer<FluidState> palettedContainer = fluidStateContainers.get(index);
                    if (palettedContainer != null) {
                        return palettedContainer.get(x & 15, y & 15, z & 15);
                    }
                }
                return Fluids.EMPTY.getDefaultState();
            } catch (Throwable err) {
                CrashReport crashReport = CrashReport.create(err, "Getting fluid state");
                CrashReportSection crashReportSection = crashReport.addElement("Fluid being got");
                crashReportSection.add("Location", () -> CrashReportSection.createPositionString(chunk, x, y, z));
                throw new CrashException(crashReport);
            }
        }
    }

    public static boolean worldSetFluidState(World world, BlockPos pos, FluidState state, int flags, int maxDepth) {
        if (world.isOutOfHeightLimit(pos)) {
            return false;
        } else if (!world.isClient && world.isDebugWorld()) {
            return false;
        } else {
            WorldChunk worldChunk = world.getWorldChunk(pos);
            FluidState replacedState = ((FluidHelper.ForChunk) worldChunk).setFluidState(pos, state);
            if (replacedState == null) {
                return false;
            } else {
                FluidState placedState = world.getFluidState(pos);
                if ((flags & Block.SKIP_LIGHTING_UPDATES) == 0) {
                    world.getProfiler().push("queueCheckLight");
                    world.getChunkManager().getLightingProvider().checkBlock(pos);
                    world.getProfiler().pop();
                }

                if (placedState == state) {
                    if (replacedState != placedState) {
                        world.scheduleBlockRerenderIfNeeded(pos, replacedState.getBlockState(), state.getBlockState());
                    }

                    if ((flags & Block.NOTIFY_LISTENERS) != 0
                            && (!world.isClient() || (flags & Block.NO_REDRAW) == 0)
                            && (world.isClient() || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                        world.updateListeners(pos, replacedState.getBlockState(), state.getBlockState(), flags);
                    }

                    if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
                        world.updateNeighbors(pos, replacedState.getBlockState().getBlock());
                    }

                    if ((flags & Block.FORCE_STATE) == 0 && maxDepth > 0) {
                        int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
                        state.getBlockState().updateNeighbors(world, pos, i, maxDepth - 1);
                    }
                    //TODO world onFluidChanged Listener
                    world.onBlockChanged(pos, replacedState.getBlockState(), placedState.getBlockState());
                }

                return true;
            }
        }
    }

    @Deprecated
    static void redirectWorldSetBlockState(World world, BlockPos pos, BlockState state, int flags, int maxUpdateDepth, BlockState oldState, WorldChunk worldChunk) {
        BlockState placedState = world.getBlockState(pos);
        if (placedState != state && state.getBlock() instanceof FluidBlock) {
            ((FluidHelper.ForWorld)world).setFluidState(pos, state.getFluidState());
            if ((flags & Block.NOTIFY_LISTENERS) != 0
                    && (!world.isClient || (flags & Block.NO_REDRAW) == 0)
                    && (world.isClient || worldChunk.getLevelType() != null
                    && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                world.updateListeners(pos, oldState, state, flags);
            }

            if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
                world.updateNeighbors(pos, oldState.getBlock());
            }

            if ((flags & Block.FORCE_STATE) == 0 && maxUpdateDepth > 0) {
                int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
                oldState.prepare(world, pos, i, maxUpdateDepth - 1);
                state.updateNeighbors(world, pos, i, maxUpdateDepth - 1);
                state.prepare(world, pos, i, maxUpdateDepth - 1);
            }
        }
    }

    public static @Nullable FluidState chunkSetFluidState(WorldChunk worldChunk, BlockPos pos, FluidState state, Heightmap worldSurfaceHeightMap) {
        int y = pos.getY();
        ChunkSection chunkSection = worldChunk.getSection(worldChunk.getSectionIndex(y));
        boolean wasEmpty = chunkSection.isEmpty();
        if (wasEmpty && state.isEmpty()) {
            return null;
        }
        else {
            int localX = pos.getX() & 15;
            int localY = y & 15;
            int localZ = pos.getZ() & 15;
            FluidState replacedState = ((FluidHelper.ForChunkSection) chunkSection).setFluidStateLocally(localX, localY, localZ, state);
            if (replacedState == state) {
                return null;
            }
            else {
                Fluid fluid = state.getFluid();
                worldSurfaceHeightMap.trackUpdate(localX, y, localZ, state.getBlockState());
                boolean stillEmpty = chunkSection.isEmpty();
                World world = worldChunk.getWorld();
                if (wasEmpty != stillEmpty) {
                    world.getChunkManager().getLightingProvider().setSectionStatus(pos, stillEmpty);
                }

                if (!world.isClient) {
                    replacedState.getBlockState().onStateReplaced(world, pos, state.getBlockState(), false);
                }

                if (!chunkSection.getFluidState(localX, localY, localZ).isOf(fluid)) {
                    return null;
                } else {
                    if (!world.isClient) {
                        state.getBlockState().onBlockAdded(world, pos, replacedState.getBlockState(), false);
                    }

                    worldChunk.setNeedsSaving(true);
                    return replacedState;
                }
            }
        }
    }

    public static void onChunkSetBlockState(WorldChunk worldChunk, BlockPos pos, BlockState blockState) {
        if (blockState.getBlock() instanceof Waterloggable) {
            BlockState oldState = worldChunk.getBlockState(pos);
            if (blockState.get(Properties.WATERLOGGED)){
                ((FluidHelper.ForChunk) worldChunk).setFluidState(pos, Fluids.WATER.getDefaultState());
            }
            else if (oldState.isOf(blockState.getBlock()) && oldState.get(Properties.WATERLOGGED)){
                ((FluidHelper.ForChunk) worldChunk).setFluidState(pos, Fluids.EMPTY.getDefaultState());
            }
        }
        else if (blockState.isSolidBlock(worldChunk.getWorld(), pos)) {
            ((FluidHelper.ForChunk)worldChunk).setFluidState(pos, Fluids.EMPTY.getDefaultState());
        }
    }

    public static void sendToPlayersWatching(ChunkHolder.PlayersWatchingChunkProvider provider, ChunkPos chunkPos, FabricPacket packet) {
        provider.getPlayersWatchingChunk(chunkPos, false).forEach(player ->
                ServerPlayNetworking.send(player, packet));
    }

    public static void onSingleFluidUpdate(SingleFluidUpdateS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        ((FluidHelper.ForWorld) player.clientWorld).setFluidState(packet.pos, packet.fluidState, 0b10011, 512);
    }

    public static void onChunkFluidUpdate(ChunkFluidUpdateS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        packet.forEach((pos, state) -> ((FluidHelper.ForWorld) player.clientWorld).setFluidState(pos, state, 0b10011, 512));
    }

    public static Optional<TypedActionResult<ItemStack>> onBucketItemUse(BucketItem instanceItem, World world, PlayerEntity user, Hand hand, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        FluidState fluidState = world.getFluidState(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        ItemStack handStack = user.getStackInHand(hand);
        if (fluidState.isStill()) {
            Item gottenItem = fluidState.getFluid().getBucketItem();
            if (handStack.getItem() instanceof FluidHelper.ForBucketItem bucketItem && bucketItem.getFluid() == Fluids.EMPTY) {
                if (blockState.getBlock() instanceof Waterloggable && blockState.get(Properties.WATERLOGGED)){
                    world.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, false));
                }
                else {
                    ((FluidHelper.ForWorld) world).setFluidState(blockPos, Fluids.EMPTY.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD, 512);
                }
                user.incrementStat(Stats.USED.getOrCreateStat(instanceItem));
                fluidState.getFluid().getBucketFillSound().ifPresent((sound) ->
                        user.playSound(sound, 1.0F, 1.0F));
                world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                ItemStack outputStack = new ItemStack(gottenItem);
                ItemStack stackInHandAfter = ItemUsage.exchangeStack(handStack, user, outputStack);
                if (!world.isClient) {
                    Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity) user, outputStack);
                }
                return Optional.of(TypedActionResult.success(stackInHandAfter, world.isClient()));
            }
        }
        return Optional.empty();
    }

    public static Optional<Boolean> onBucketItemPlacedFluid(BucketItem instanceItem, PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult){
        boolean nullHit = hitResult == null;
        if (!nullHit) pos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        boolean isSolid = blockState.isSolidBlock(world, pos);
        Fluid fluid = ((FluidHelper.ForBucketItem) instanceItem).getFluid();
        if (isSolid) {
            if (nullHit) return Optional.of(false);
            return Optional.of(instanceItem.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null));
        }
        else {
            if (world.getDimension().ultrawarm() && fluid.matchesType(Fluids.WATER)) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                for(int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
                }
                return Optional.of(true);
            }
            else {
                if (!((FluidHelper.ForWorld)world).setFluidState(pos, fluid.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD, 512) && !world.getFluidState(pos).isStill()) {
                    return Optional.of(false);
                }
                else {
                    ((FluidHelper.ForBucketItem) instanceItem).callPlayEmptyingSound(player, world, pos);
                    return Optional.of(true);
                }
            }
        }
    }
}
