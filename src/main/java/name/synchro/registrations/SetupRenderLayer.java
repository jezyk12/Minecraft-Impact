package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
@Environment(EnvType.CLIENT)
public final class SetupRenderLayer {
    public static void setupAll(){
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.CABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.BUSH_WITH_FLOWERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.TOMATO_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.PEANUT_BUSH_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.BANANA_LEAVES, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.WATER_VAPOR_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.WATER_VAPOR_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.SULFURIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.CHLORIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.STRONGLY_OXIDIZING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(RegisterFluids.STRONGLY_REDUCING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.BANANA_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistered.STRAW_NEST, RenderLayer.getCutout());
        Synchro.LOGGER.debug("Set up all render layers for "+Synchro.MOD_ID);
    }
}
