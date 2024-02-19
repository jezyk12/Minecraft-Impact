package name.synchro.synchroBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GasBlock extends Block {
    protected final AbstractGas gas;
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;

    public GasBlock(Settings settings, AbstractGas gas) {
        super(settings);
        this.gas = gas;
        setDefaultState(getDefaultState().with(LEVEL,8));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.gas.getDefaultState().with(AbstractGas.LEVEL,state.get(LEVEL));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
        //return stateFrom.isOf(state.getBlock()) && state.getFluidState().getLevel() == stateFrom.getFluidState().getLevel();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(oldState.getBlock())) {
            world.scheduleFluidTick(pos, gas, gas.getTickRate(world));
        }
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        world.scheduleFluidTick(pos,gas,gas.getTickRate(world));
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
}
