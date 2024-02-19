package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SetupRenderLayer {
    public static void setupAll(){
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.CABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BUSH_WITH_FLOWERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.TOMATO_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.WATER_VAPOR_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.WATER_VAPOR_GAS,RenderLayer.getTranslucent());
        Synchro.LOGGER.debug("Set up all render layers for "+Synchro.MOD_ID);
    }
}
