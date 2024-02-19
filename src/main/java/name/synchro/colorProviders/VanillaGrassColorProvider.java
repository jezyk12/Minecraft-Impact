package name.synchro.colorProviders;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class VanillaGrassColorProvider implements BlockColorProvider {
    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        if (world != null) {
            return world.getColor(pos, BiomeColors.GRASS_COLOR);
        }
        else return -1;
    }
}
