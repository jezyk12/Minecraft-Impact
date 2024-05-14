package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
@Environment(EnvType.CLIENT)
public class SetupRenderLayer {
    public static void setupAll(){
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.CABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BUSH_WITH_FLOWERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.TOMATO_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.PEANUT_BUSH_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BANANA_LEAVES, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.WATER_VAPOR_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.WATER_VAPOR_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.SULFURIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.CHLORIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.STRONGLY_OXIDIZING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.STRONGLY_REDUCING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.BANANA_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.STRAW_NEST, RenderLayer.getCutout());
        Synchro.LOGGER.debug("Set up all render layers for "+Synchro.MOD_ID);
    }
}
