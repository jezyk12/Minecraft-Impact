package name.synchro.synchroBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FertileDirtBlock extends Block {
    public FertileDirtBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState upState = world.getBlockState(pos.up());
        if (upState.getBlock() instanceof PlantBlock && upState.getBlock().hasRandomTicks(upState)){
            upState.getBlock().randomTick(upState,world,pos.up(),random);
        }
    }
}
