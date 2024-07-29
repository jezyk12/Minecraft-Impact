package name.synchro.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FertileFarmlandBlock extends AbstractFarmlandBlock {
    public FertileFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        BlockState upState = world.getBlockState(pos.up());
        if (upState.getBlock() instanceof PlantBlock && upState.hasRandomTicks()){
            upState.randomTick(world,pos.up(),random);
        }
    }
}
