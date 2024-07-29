package name.synchro.colorProviders;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BushWithFlowersColorProvider implements BlockColorProvider {
    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        switch (tintIndex) {
            case 0 -> {
                Random random;
                if (pos != null) {
                    random = new Random(pos.asLong());
                    return random.nextInt(0x1000000);
                } else return -1;
            }
            case 1 -> {
                if (world != null) {
                    return world.getColor(pos, BiomeColors.GRASS_COLOR);
                }
                else return -1;
            }
            default -> {
                return  -1;
            }
        }
    }
}
