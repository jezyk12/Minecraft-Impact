package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
@Environment(EnvType.CLIENT)
public final class SetupRenderLayer {
    public static void setupAll(){
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BUSH_WITH_FLOWERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TOMATO_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEANUT_BUSH_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BANANA_LEAVES, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WATER_VAPOR_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.WATER_VAPOR_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.SULFURIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.CHLORIC_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.STRONGLY_OXIDIZING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.STRONGLY_REDUCING_GAS,RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BANANA_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STRAW_NEST, RenderLayer.getCutout());
        Synchro.LOGGER.debug("Set up all render layers for "+Synchro.MOD_ID);
    }
}
