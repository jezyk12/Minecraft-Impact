package name.synchro.colorProviders;

import name.synchro.blocks.Rock;
import name.synchro.util.Metals;
import name.synchro.util.MetalsComponentsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class RockColorProvider implements BlockColorProvider {
    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        if (pos == null) {
            return -1;
        }
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld != null) {
            switch (state.get(Rock.ROCK_TYPE)) {
                case NATURAL -> {
                    Metals.Metal metal = MetalsComponentsHelper.createMetalList(clientWorld, state, pos).get(0);
                    return metal.color();
                }
                case ARTIFICIAL -> {
                    return -1;
                }
                case MUTABLE -> {
                    BlockEntity blockEntity = clientWorld.getBlockEntity(pos);
                    if (blockEntity instanceof Rock.MutableBlockEntity rockBlockEntity) {
                        Map<Integer, Integer> contents = rockBlockEntity.metalContent;
                        if (contents != null) {
                            Metals.Metal metal = getMaxContentMetal(contents, ((Metals.Provider) clientWorld).getMetals());
                            return metal.color();
                        }
                    }
                    return -1;
                }
            }
        }
        return -1;
    }

    private static Metals.Metal getMaxContentMetal(Map<Integer, Integer> contents, Metals metals) {
        Metals.Metal metal = Metals.EMPTY_METAL;
        float max = -1f;
        for (int numId: contents.keySet()) {
            if (contents.get(numId) > max) {
                max = contents.get(numId);
                metal = metals.getVariants().get(numId);
            }
        }
        return metal;
    }
}
