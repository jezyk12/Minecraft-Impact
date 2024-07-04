package name.synchro.blocks;

import name.synchro.registrations.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BananaLeavesBlock extends LeavesBlock {
    public BananaLeavesBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        int i;
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if ((i = getDistanceFromStem(neighborState) + 1) != 1 || state.get(DISTANCE) != i) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return state;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, updateDistanceFromStems(state, world, pos), Block.NOTIFY_ALL);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockState blockState = this.getDefaultState().with(PERSISTENT, true).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return updateDistanceFromStems(blockState, ctx.getWorld(), ctx.getBlockPos());
    }

    private static BlockState updateDistanceFromStems(BlockState state, World world, BlockPos pos){
        int i = 7;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.set(pos, direction);
            i = Math.min(i, getDistanceFromStem(world.getBlockState(mutable)) + 1);
            if (i == 1) break;
        }
        return state.with(DISTANCE, i);
    }

    private static int getDistanceFromStem(BlockState state) {
        if (state.isOf(ModBlocks.BANANA_STEM)) {
            return 0;
        }
        if (state.isOf(ModBlocks.BANANA_LEAVES)) {
            return state.get(DISTANCE);
        }
        return 7;
    }
}
