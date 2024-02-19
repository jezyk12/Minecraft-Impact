package name.synchro.synchroBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractGas extends Fluid {
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;
    public int upsideMinGradient;
    public int downsideMinGradient;
    public int horizontalMinGradient;

    public AbstractGas(int upsideMinGradient, int downsideMinGradient, int horizontalMinGradient) {
        this.upsideMinGradient = upsideMinGradient;
        this.downsideMinGradient = downsideMinGradient;
        this.horizontalMinGradient = horizontalMinGradient;
        setDefaultState(getDefaultState().with(LEVEL,8));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
        tryDiffuse(world,pos,state);
    }

    protected void tryDiffuse(World world, BlockPos pos, FluidState state){
        int thisLevel = state.getLevel();
        for (Direction direction: Direction.values()){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborBlockState = world.getBlockState(neighborPos);
            FluidState neighborFluidState = world.getFluidState(neighborPos);
            int neighborLevel;
            if (neighborBlockState.isAir()){
                neighborLevel = 0;
            }
            else if (neighborBlockState.isOf(toBlockState(state).getBlock())){
                neighborLevel = neighborFluidState.getLevel();
            }
            else continue;
            int gradient = thisLevel - neighborLevel;
            if (direction == Direction.UP){
                if (gradient >= upsideMinGradient) diffuse(world,pos,state,neighborPos,neighborFluidState,gradient);
            }
            else if (direction == Direction.DOWN){
                if (gradient >= downsideMinGradient) diffuse(world,pos,state,neighborPos,neighborFluidState,gradient);
            }
            else {
                if (gradient >= horizontalMinGradient) diffuse(world,pos,state,neighborPos,neighborFluidState,gradient);
            }
        }
    }

    protected void diffuse(World world, BlockPos thisPos, FluidState thisState, BlockPos neighborPos, FluidState neighborState, int gradient){
        int thisLevel = thisState.getLevel();
        if (gradient > 1){
            world.setBlockState(thisPos, toBlockState(thisState).with(LEVEL,thisLevel - 1));
            world.setBlockState(neighborPos, toBlockState(thisState).with(GasBlock.LEVEL, thisLevel - gradient + 1));
            world.scheduleFluidTick(thisPos, thisState.getFluid(), getTickRate(world));
            world.scheduleFluidTick(neighborPos, neighborState.getFluid(), getTickRate(world));
        }
    }

    @Override
    public Item getBucketItem() {
        return Items.BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        return Vec3d.ZERO;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 2;
    }

    @Override
    protected float getBlastResistance() {
        return 100f;
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        return 1f;
    }

    @Override
    public float getHeight(FluidState state) {
        return 1f;
    }

    @Override
    protected abstract BlockState toBlockState(FluidState state);

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState state) {
        return state.get(LEVEL);
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }
}
