package name.synchro.blocks;

import name.synchro.registrations.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DirtCanalBlock extends Block implements Waterloggable {
    public enum CanalState implements StringIdentifiable {
        DEFAULT("default"),
        LINKED("linked");
        private final String name;
        CanalState(String name) {
            this.name = name;
        }
        public String toString() {
            return this.asString();
        }
        @Override
        public String asString() {
            return this.name;
        }
    }
    public static final EnumProperty<CanalState> NORTH_CANAL_STATE = EnumProperty.of("north",CanalState.class);
    public static final EnumProperty<CanalState> SOUTH_CANAL_STATE = EnumProperty.of("south",CanalState.class);
    public static final EnumProperty<CanalState> EAST_CANAL_STATE = EnumProperty.of("east",CanalState.class);
    public static final EnumProperty<CanalState> WEST_CANAL_STATE = EnumProperty.of("west",CanalState.class);
    public static final HashMap<Direction,EnumProperty<CanalState>> DIRECTION_CANAL_PROPERTY_MAP = new HashMap<>();
    public static void init(){
        DIRECTION_CANAL_PROPERTY_MAP.put(Direction.WEST,WEST_CANAL_STATE);
        DIRECTION_CANAL_PROPERTY_MAP.put(Direction.EAST,EAST_CANAL_STATE);
        DIRECTION_CANAL_PROPERTY_MAP.put(Direction.NORTH,NORTH_CANAL_STATE);
        DIRECTION_CANAL_PROPERTY_MAP.put(Direction.SOUTH,SOUTH_CANAL_STATE);
    }
    public static final VoxelShape BASE_CUBE = VoxelShapes.cuboid(0/16f,0/16f,0/16f,16/16f,6/16f,16/16f);
    public static final VoxelShape EAST_CUBE = VoxelShapes.cuboid(14/16f,6/16f,0/16f,16/16f,16/16f,16/16f);
    public static final VoxelShape WEST_CUBE = VoxelShapes.cuboid(0/16f,6/16f,0/16f,2/16f,16/16f,16/16f);
    public static final VoxelShape NORTH_CUBE = VoxelShapes.cuboid(0/16f,6/16f,0/16f,16/16f,16/16f,2/16f);
    public static final VoxelShape SOUTH_CUBE = VoxelShapes.cuboid(0/16f,6/16f,14/16f,16/16f,16/16f,16/16f);
    public DirtCanalBlock(Settings settings) {
        super(settings);
        init();
        setDefaultState(getDefaultState()
                .with(NORTH_CANAL_STATE,CanalState.DEFAULT)
                .with(SOUTH_CANAL_STATE,CanalState.DEFAULT)
                .with(EAST_CANAL_STATE,CanalState.DEFAULT)
                .with(WEST_CANAL_STATE,CanalState.DEFAULT)
                .with(Properties.WATERLOGGED,false));
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape result = BASE_CUBE;
        if(state.get(EAST_CANAL_STATE)==CanalState.DEFAULT) result=VoxelShapes.union(result,EAST_CUBE);
        if(state.get(WEST_CANAL_STATE)==CanalState.DEFAULT) result=VoxelShapes.union(result,WEST_CUBE);
        if(state.get(NORTH_CANAL_STATE)==CanalState.DEFAULT) result=VoxelShapes.union(result,NORTH_CUBE);
        if(state.get(SOUTH_CANAL_STATE)==CanalState.DEFAULT) result=VoxelShapes.union(result,SOUTH_CUBE);
        return result;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH_CANAL_STATE,SOUTH_CANAL_STATE,EAST_CANAL_STATE,WEST_CANAL_STATE, Properties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(!state.isOf(newState.getBlock())){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private static boolean shouldFillWater(World world, BlockState state, BlockPos pos){
        if (world.getFluidState(pos.up()).isIn(FluidTags.WATER)&&
        !VoxelShapes.adjacentSidesCoverSquare(state.getCollisionShape(world,pos),
                world.getBlockState(pos.up()).getCollisionShape(world,pos.up()),
                Direction.UP)) return true;
        for (Direction direction: Direction.Type.HORIZONTAL){
            if (state.get(DIRECTION_CANAL_PROPERTY_MAP.get(direction))==CanalState.LINKED){
                FluidState fluidState = world.getFluidState(pos.offset(direction));
                if (fluidState.isEqualAndStill(Fluids.WATER)) return true;
                BlockState neighborState = world.getBlockState(pos.offset(direction));
                if (neighborState.isOf(ModBlocks.DIRT_CANAL)){
                    if (neighborState.get(Properties.WATERLOGGED)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        for (Direction direction: Direction.Type.HORIZONTAL){
            state = getLinkState(state,direction,world.getBlockState(pos.offset(direction)));
        }
        world.setBlockState(pos,state);
        world.scheduleBlockTick(pos,this,1);
        super.onBlockAdded(state, world, pos, oldState, false);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (shouldFillWater(world,state,pos)){
            world.setBlockState(pos,state.with(Properties.WATERLOGGED,true));
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
       if (DIRECTION_CANAL_PROPERTY_MAP.containsKey(direction)) {
           state = getLinkState(state, direction, neighborState);
       }
       world.scheduleBlockTick(pos,this,1);
       return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static BlockState getLinkState(BlockState state, Direction direction, BlockState neighborState) {
        if (neighborState.isOf(ModBlocks.DIRT_CANAL)||neighborState.isOf(Blocks.WATER)) {
            state = state.with(DIRECTION_CANAL_PROPERTY_MAP.get(direction), CanalState.LINKED);
        } else {
            state = state.with(DIRECTION_CANAL_PROPERTY_MAP.get(direction), CanalState.DEFAULT);
        }
        return state;
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        world.scheduleBlockTick(pos,this,1);
        if (state.get(Properties.WATERLOGGED)) {
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), Block.NOTIFY_ALL);
            for (Direction direction:Direction.Type.HORIZONTAL){
                if (state.get(DIRECTION_CANAL_PROPERTY_MAP.get(direction))==CanalState.LINKED){
                    BlockPos neighborPos = pos.offset(direction);
                    if (world.getBlockState(neighborPos).isOf(ModBlocks.DIRT_CANAL)){
                        tryDrainFluid(world,neighborPos,world.getBlockState(neighborPos));
                    }
                }
            }
            return new ItemStack(Items.WATER_BUCKET);
        }
        return ItemStack.EMPTY;
    }
}
