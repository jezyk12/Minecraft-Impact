package name.synchro.blocks;

import name.synchro.registrations.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BananaStem extends Block {
    public BananaStem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tryGrowBanana(world, pos, random);
    }

    private void tryGrowBanana(ServerWorld world, BlockPos pos, Random random){
        if (random.nextInt(8) == 0) {
            Direction chosenDirection = Direction.Type.HORIZONTAL.random(random);
            BlockState chosenState = world.getBlockState(pos.offset(chosenDirection));
            BlockState shouldBeLeaves = world.getBlockState(pos.offset(chosenDirection).up());
            if ((chosenState.isAir() || chosenState.isIn(BlockTags.REPLACEABLE_PLANTS)) &&
                    shouldBeLeaves.isOf(RegisterBlocks.BANANA_LEAVES) &&
                    !shouldBeLeaves.get(BananaLeavesBlock.PERSISTENT)) {
                int bananas = 0;
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    if (direction.equals(chosenDirection)) break;
                    BlockState neighborState = world.getBlockState(pos.offset(direction));
                    if (neighborState.isOf(RegisterBlocks.BANANA_BLOCK)) {
                        bananas++;
                    }
                }
                int dy = 1;
                while (pos.getY() - dy > world.getBottomY()){
                    if (world.getBlockState(pos.down(dy)).isOf(RegisterBlocks.BANANA_STEM)){
                        dy++;
                    }
                    else if (world.getBlockState(pos.down(dy)).isOf(RegisterBlocks.FERTILE_DIRT)){
                        bananas = 0;
                        break;
                    }
                    else break;
                }
                if (bananas == 0 || random.nextInt(bananas) == 0) {
                    world.setBlockState(pos.offset(chosenDirection), RegisterBlocks.BANANA_BLOCK.getDefaultState().with(BananaBlock.AGE, 0));
                }
            }
        }
    }
}
