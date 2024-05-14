package name.synchro.blocks;

import name.synchro.fluids.gases.Gas;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GasBlock extends Block {
    protected final Gas gas;

    public GasBlock(Settings settings, Gas gas) {
        super(settings);
        this.gas = gas;
        setDefaultState(getDefaultState().with(Gas.LEVEL, Gas.MAX_LEVEL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Gas.LEVEL);
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
        return this.gas.getDefaultState().with(Gas.LEVEL,state.get(Gas.LEVEL));
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
