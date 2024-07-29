package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.blockEntityRenderers.DebugBlockEntityRenderer;
import name.synchro.blockEntityRenderers.MillstoneBlockEntityRenderer;
import name.synchro.blockEntityRenderers.StrawNestBlockEntityRenderer;
import name.synchro.registrations.ModBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public final class BlockEntityRenderers {
    public static void registerAll() {
        BlockEntityRendererFactories.register(ModBlockEntities.DEBUG_BLOCK_ENTITY, DebugBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MILLSTONE_BLOCK_ENTITY, MillstoneBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.STRAW_NEST_BLOCK_ENTITY, StrawNestBlockEntityRenderer::new);
        Synchro.LOGGER.debug("Registered mod block entity renderers for" + Synchro.MOD_ID);
    }
}
