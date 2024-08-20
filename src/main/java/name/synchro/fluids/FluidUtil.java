package name.synchro.fluids;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.DoubleMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import name.synchro.registrations.ModTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
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
            palettedContainer = dataResult.getOrThrow();
        } else {
            palettedContainer = createFluidStatePaletteContainer();
        }
        return palettedContainer;
    }

    public static void serialize(NbtCompound nbt, ChunkSection chunkSection) {
        DataResult<NbtElement> dataResult = CODEC.encodeStart(NbtOps.INSTANCE, ((FluidHelper.ForChunkSection) chunkSection).synchro$getFluidStateContainer());
        nbt.put(KEY_FLUID_STATES, dataResult.getOrThrow());
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

    public static boolean worldSetFluidState(World world, BlockPos pos, FluidState fluidState, int flags, int maxDepth) {
        BlockState originalBlockState = world.getBlockState(pos);
        BlockState finalBlockState = originalBlockState;
        boolean skipSetFluidState = false;
        if (originalBlockState.isAir() || originalBlockState.getBlock() instanceof FluidBlock){
            if (fluidState.isEmpty()) {
                finalBlockState = Blocks.AIR.getDefaultState();
            }
            else {
                finalBlockState = fluidState.getBlockState();
            }
            skipSetFluidState = true;
        }
        else if (originalBlockState.getBlock() instanceof Waterloggable){
            if (fluidState.isEqualAndStill(Fluids.WATER)){
                finalBlockState = originalBlockState.with(Properties.WATERLOGGED, true);
                skipSetFluidState = true;
            }
            else if (originalBlockState.get(Properties.WATERLOGGED)){
                finalBlockState = originalBlockState.with(Properties.WATERLOGGED, false);
                world.setBlockState(pos, finalBlockState);
            }
        }
        if (skipSetFluidState){
            return world.setBlockState(pos, finalBlockState);
        }
        if (world.isOutOfHeightLimit(pos)) {
            return false;
        } else if (!world.isClient && world.isDebugWorld()) {
            return false;
        } else {
            WorldChunk worldChunk = world.getWorldChunk(pos);
            FluidState replacedState = ((FluidHelper.ForChunk) worldChunk).synchro$setFluidState(pos, fluidState);
            if (replacedState == null) {
                return false;
            } else {
                FluidState placedState = world.getFluidState(pos);
                if (placedState == fluidState) {
                    if (replacedState != placedState) {
                        world.scheduleBlockRerenderIfNeeded(pos, replacedState.getBlockState(), fluidState.getBlockState());
                    }

                    if ((flags & Block.NOTIFY_LISTENERS) != 0
                            && (!world.isClient() || (flags & Block.NO_REDRAW) == 0)
                            && (world.isClient() || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkLevelType.BLOCK_TICKING))) {
                        world.updateListeners(pos, originalBlockState, finalBlockState, flags);
                    }

                    if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
                        world.updateNeighbors(pos, finalBlockState.getBlock());
                    }

                    if ((flags & Block.FORCE_STATE) == 0 && maxDepth > 0) {
                        int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
                        finalBlockState.updateNeighbors(world, pos, i, maxDepth - 1);
                    }
                    world.onBlockChanged(pos, replacedState.getBlockState(), placedState.getBlockState());
                }

                return true;
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
            FluidState replacedState = ((FluidHelper.ForChunkSection) chunkSection).synchro$setFluidStateLocally(localX, localY, localZ, state);
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
        if (canBlockCoverFluid(worldChunk.getWorld(), pos, blockState, worldChunk.getFluidState(pos).getHeight())) {
            ((FluidHelper.ForChunk) worldChunk).synchro$setFluidState(pos, Fluids.EMPTY.getDefaultState());
        }
    }

    public static boolean canBlockCoverFluid(BlockView world, BlockPos pos, BlockState blockState, float height){
        if (blockState.isAir()) return false;
        if (blockState.isIn(ModTags.CAN_STORE_FLUID)) return false;
        if (blockState.isIn(ModTags.NEVER_FILL_FLUID)) return true;
        if (blockState.isFullCube(world, pos)) return true;
        VoxelShape shape = blockState.getCollisionShape(world, pos);
        return !VoxelShapes.matchesAnywhere(
                VoxelShapes.combine(VoxelShapes.fullCube(), shape, BooleanBiFunction.ONLY_FIRST),
                VoxelShapes.cuboid(0, 0, 0, 1, height, 1), BooleanBiFunction.AND);
    }

    public static void sendToPlayersWatching(ChunkHolder.PlayersWatchingChunkProvider provider, ChunkPos chunkPos, CustomPayload payload) {
        provider.getPlayersWatchingChunk(chunkPos, false).forEach(player ->
                ServerPlayNetworking.send(player, payload));
    }

    public static Optional<TypedActionResult<ItemStack>> onBucketItemUse(BucketItem instanceItem, World world, PlayerEntity user, Hand hand, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        FluidState fluidState = world.getFluidState(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        ItemStack handStack = user.getStackInHand(hand);
        if (fluidState.isStill()) {
            Item gottenItem = fluidState.getFluid().getBucketItem();
            if (handStack.getItem() instanceof FluidHelper.ForBucketItem bucketItem && bucketItem.synchro$getFluid() == Fluids.EMPTY) {
                if (blockState.getBlock() instanceof Waterloggable && blockState.get(Properties.WATERLOGGED)){
                    world.setBlockState(blockPos, blockState.with(Properties.WATERLOGGED, false));
                }
                else {
                    ((FluidHelper.ForWorld) world).synchro$setFluidState(blockPos, Fluids.EMPTY.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD, 512);
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
        boolean covered = canBlockCoverFluid(world, pos, blockState, 8 / 9f);
        Fluid fluid = ((FluidHelper.ForBucketItem) instanceItem).synchro$getFluid();
        if (covered) {
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
                if (!((FluidHelper.ForWorld)world).synchro$setFluidState(pos, fluid.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD, 512) && !world.getFluidState(pos).isStill()) {
                    return Optional.of(false);
                }
                else {
                    ((FluidHelper.ForBucketItem) instanceItem).synchro$callPlayEmptyingSound(player, world, pos);
                    return Optional.of(true);
                }
            }
        }
    }

    public static boolean onLavaFormsStone(WorldAccess world, BlockPos pos) {
        return onSolidifyLava(world, pos);
    }

    public static boolean onLavaFormsObsidian(World world, BlockPos pos) {
        return onSolidifyLava(world, pos);
    }

    public static boolean onLavaFormsCobblestone(World world, BlockPos pos) {
        return onSolidifyLava(world, pos);
    }

    public static boolean onLavaFormsBasalt(World world, BlockPos pos) {
        return onSolidifyLava(world, pos);
    }

    private static boolean onSolidifyLava(WorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isAir() && !(state.getBlock() instanceof FluidBlock)) {
            if (state.isFullCube(world, pos)) return false;
            world.breakBlock(pos, true);
        }
        return true;
    }

    public static boolean canBlockWashAway(BlockState state, Fluid fluid){
        if (fluid.matchesType(Fluids.WATER)){
            return state.isIn(ModTags.WASH_AWAY_BY_WATER);
        }
        else if (fluid.matchesType(Fluids.LAVA)){
            return state.isIn(ModTags.WASH_AWAY_BY_LAVA);
        }
        else return false;
    }

    public static boolean canBlockCoexistWith(BlockState state, Fluid fluid){
        if (fluid.matchesType(Fluids.WATER)){
            return !state.isIn(ModTags.DESTROY_IN_WATER);
        }
        else if (fluid.matchesType(Fluids.LAVA)){
            return !state.isIn(ModTags.BURN_AWAY_IN_LAVA);
        }
        else return true;
    }

    private static final Map<Direction, Box> SLICE_BOXES = ImmutableMap.<Direction, Box>builder()
            .put(Direction.NORTH, new Box(0,0,0,1,1,1 / 64d))
            .put(Direction.SOUTH, new Box(0,0,63 / 64d,1,1,1))
            .put(Direction.WEST, new Box(0,0,0,1 / 64d,1,1))
            .put(Direction.EAST, new Box(63 / 64d,0,0,1,1,1))
            .build();

    private static boolean canShapeFluidFlow(double height, boolean flowOut, VoxelShape shape, Direction flowDirection) {
        if (flowDirection == Direction.DOWN) return true;
        else if (flowDirection == Direction.UP) return false;
        else if (shape.equals(VoxelShapes.fullCube()) || height < 3 / 18f) {
            return false;
        } else {
            Direction checkDirection = flowOut ? flowDirection : flowDirection.getOpposite();
            Direction.Axis axis = checkDirection.getAxis();
            Direction.AxisDirection flowFrom = checkDirection.getDirection();
            if (flowFrom.offset() > 0){
                if (!DoubleMath.fuzzyEquals(shape.getMax(axis), 1.0, 1.0E-7)) return true;
            }
            else {
                if (!DoubleMath.fuzzyEquals(shape.getMin(axis), 0.0, 1.0E-7)) return true;
            }
            VoxelShape slice = VoxelShapes.cuboid(SLICE_BOXES.get(checkDirection).withMaxY(height));
            return VoxelShapes.matchesAnywhere(
                    VoxelShapes.combine(VoxelShapes.fullCube(), shape, BooleanBiFunction.ONLY_FIRST),
                    slice, BooleanBiFunction.AND);
        }
    }

    public static Optional<Boolean> judgeCanFlow(BlockView world, BlockPos fromPos, BlockState fromBlockState, Direction flowDirection, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid, boolean original) {
        if (!original) return Optional.empty();
        if (toBlockState.isIn(ModTags.NEVER_FILL_FLUID)) return Optional.of(false);
        if (canBlockWashAway(toBlockState, fluid)){
            return Optional.of(true);
        }
        else {
            final double height = world.getFluidState(fromPos).getHeight();
            if (canShapeFluidFlow(height, true, fromBlockState.getCollisionShape(world, fromPos), flowDirection)
                    && canShapeFluidFlow(height, false, toBlockState.getCollisionShape(world, toPos), flowDirection)){
                return Optional.of(true);
            }
        }
        return Optional.of(false);
    }

    public static boolean isStillWaterForBubbles(WorldAccess worldAccess, BlockPos pos) {
        BlockState blockState = worldAccess.getBlockState(pos);
        FluidState fluidState = worldAccess.getFluidState(pos);
        return blockState.isOf(Blocks.BUBBLE_COLUMN) || fluidState.isOf(Fluids.WATER) && fluidState.isStill();
    }

    public static void onBlockCoexistWithFluid(World world, BlockPos pos, FluidState state) {
        BlockState blockState = blockOperation(world, pos, state);
        fluidReact(world, pos, blockState);
    }

    private static @NotNull BlockState blockOperation(World world, BlockPos pos, FluidState state) {
        BlockState blockState = world.getBlockState(pos);
        BlockState finalState = blockState;
        if (blockState.getBlock() instanceof CampfireBlock){
            if (blockState.get(CampfireBlock.LIT) && state.isIn(FluidTags.WATER) && state.getLevel() > 2){
                BlockState unlitCampFire = blockState.with(CampfireBlock.LIT, false);
                world.setBlockState(pos, unlitCampFire);
                finalState = unlitCampFire;
            }
        }
        Fluid fluid = state.getFluid();
        if (!canBlockCoexistWith(blockState, fluid)){
            if (fluid.matchesType(Fluids.LAVA)){
                world.setBlockState(pos, state.getBlockState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                finalState = state.getBlockState();
            }
            else {
                world.breakBlock(pos, true);
                finalState = state.getBlockState();
            }
        }
        return finalState;
    }

    public static void fluidReact(World world, BlockPos pos, BlockState blockState){
        if (world.isClient()) return;
        if (blockState.isAir() || blockState.getBlock() instanceof FluidBlock) return;
        FluidReaction.match((ServerWorld) world, pos).forEach(action -> action.act(world, pos));

//        ModDataContainer<?> container =  ((ModDataManager.Provider)world).synchro$getModDataManager().getContents().get(FluidReactionData.ID);
//        if (container instanceof FluidReactionData fluidReactionData){
//            Map<Long, FluidReaction> map = fluidReactionData.data();
//            long key = FluidReactionData.longKey(fluidState.getFluid(), originalBlockState.getBlock());
//            FluidReaction entry = map.get(key);
//            if (entry == null) return;
//            if (!entry.match(fluidState, originalBlockState)) return;
//            for (LocationAction action: entry.actions()){
//                action.act(world, pos);
//            }
//        }
    }

}
