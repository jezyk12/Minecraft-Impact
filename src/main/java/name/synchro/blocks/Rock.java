package name.synchro.blocks;

import com.google.common.collect.ImmutableMap;
import name.synchro.api.SuitableForRock;
import name.synchro.items.OresMixture;
import name.synchro.registrations.ModBlockEntities;
import name.synchro.registrations.ModItems;
import name.synchro.util.Metals;
import name.synchro.util.NbtTags;
import name.synchro.modUtilData.DataUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Rock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Type> ROCK_TYPE = EnumProperty.of("type", Type.class);
    private static final ImmutableMap<Item, OresMixture.Type> VANILLA_SUITABLE_TOOLS =
            ImmutableMap.of(Items.WOODEN_PICKAXE, OresMixture.Type.LUMP,
                    Items.GOLDEN_PICKAXE, OresMixture.Type.LUMP,
                    Items.STONE_PICKAXE, OresMixture.Type.CRACKED,
                    Items.IRON_PICKAXE, OresMixture.Type.CRACKED,
                    Items.DIAMOND_PICKAXE, OresMixture.Type.CRUSHED,
                    Items.NETHERITE_PICKAXE, OresMixture.Type.CRUSHED);

    public Rock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ROCK_TYPE, Rock.Type.NATURAL));
    }

    public enum Type implements StringIdentifiable {
        NATURAL("natural"),
        ARTIFICIAL("artificial"),
        MUTABLE("mutable");

        final String name;
        Type(String name) {
            this.name = name;
        }
        @Override
        public String asString() {
            return this.name;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ROCK_TYPE);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        NbtCompound nbtCompound = ModItems.getNbt(ctx.getStack());
        if (nbtCompound != null && nbtCompound.contains(NbtTags.METALS_CONTENT))
            return this.getDefaultState().with(ROCK_TYPE, Rock.Type.MUTABLE);
        return this.getDefaultState().with(ROCK_TYPE, Rock.Type.ARTIFICIAL);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (state.get(ROCK_TYPE) == Rock.Type.MUTABLE) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MutableBlockEntity mutableBlockEntity) {
                NbtCompound itemStackNbt = ModItems.getNbt(itemStack);
                if (itemStackNbt != null) {
                    mutableBlockEntity.metalContent = Metals.getMetalContentFromNbt(itemStackNbt);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(ROCK_TYPE) == Rock.Type.MUTABLE) {
            return new MutableBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        this.dropLoots(state, world, pos, tool);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && player.isCreative()){
            this.dropLoots(state, (ServerWorld) world, pos, player.getStackInHand(Hand.MAIN_HAND));
        }
        return super.onBreak(world, pos, state, player);
    }

    private void dropLoots(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool){
        if (VANILLA_SUITABLE_TOOLS.containsKey(tool.getItem())) {
            boolean silkTouch = EnchantmentHelper.getLevel(DataUtil.entryOf(Enchantments.SILK_TOUCH, world.getRegistryManager()), tool) > 0;
            ItemStack stack = getDropStack(state, world, pos, silkTouch, true, VANILLA_SUITABLE_TOOLS.get(tool.getItem()));
            dropStack(world, pos, stack);
        }
        else if (tool.getItem() instanceof SuitableForRock rockTool) {
            ItemStack stack = getDropStack(state, world, pos, rockTool.dropWholeBlock(tool), rockTool.keepNbt(tool), rockTool.oresLevel(tool));
            dropStack(world, pos, stack);
        }
    }

    private ItemStack getDropStack(BlockState state, ServerWorld world, BlockPos pos, boolean wholeBlock, boolean keepNbt, OresMixture.Type type){
        ItemStack stack;
        if (wholeBlock) {
            stack = new ItemStack(state.getBlock().asItem(), 1);
            if (keepNbt) {
                NbtCompound nbt = new NbtCompound();
                setMetalContentNbtFromPos(nbt, world, state, pos);
                ModItems.setNbt(stack, nbt);
            }
        } else {
            stack = switch (type){
                case LUMP -> new ItemStack(ModItems.LUMP_ORES, 1);
                case CRACKED -> new ItemStack(ModItems.CRACKED_ORES, 1);
                case CRUSHED -> new ItemStack(ModItems.CRUSHED_ORES, 1);
                default -> ItemStack.EMPTY;
            };
            NbtCompound nbt = new NbtCompound();
            setMetalContentNbtFromPos(nbt, world, state, pos);
            ModItems.setNbt(stack, nbt);
        }
        return stack;
    }

    private void setMetalContentNbtFromPos(NbtCompound nbt, ServerWorld world, BlockState state, BlockPos pos){
        Map<Integer, Integer> content = ((Metals.Provider) world).getMetals().getContents(state, pos);
        Metals.writeMetalContentToNbt(nbt, content);
    }

    public static class MutableBlockEntity extends BlockEntity {
        public @Nullable Map<Integer, Integer> metalContent;

        public MutableBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.MUTABLE_ROCK_BLOCK_ENTITY, pos, state);
        }

        @Override
        public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
            super.readNbt(nbt, wrapperLookup);
            metalContent = Metals.getMetalContentFromNbt(nbt);
        }

        @Override
        protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
            if (metalContent != null && world != null) {
                Metals.writeMetalContentToNbt(nbt, metalContent);
            }
            super.writeNbt(nbt, wrapperLookup);
        }
    }
}
