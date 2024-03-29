package name.synchro.registrations;

import name.synchro.colorProviders.BushWithFlowersColorProvider;
import name.synchro.colorProviders.DebugColorProvider;
import name.synchro.colorProviders.RawMixedOreColorProvider;
import name.synchro.colorProviders.VanillaGrassColorProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

@Environment(EnvType.CLIENT)
public class RegisterColorProviders {
    public static void registerAll(){
        ColorProviderRegistry.ITEM.register(new RawMixedOreColorProvider(),RegisterItems.RAW_MIXED_ORE);
        ColorProviderRegistry.BLOCK.register(new BushWithFlowersColorProvider(),RegisterBlocks.BUSH_WITH_FLOWERS);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(),RegisterBlocks.BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(),RegisterBlocks.TOMATO_BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(),RegisterBlocks.BANANA_STEM);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(),RegisterBlocks.BANANA_LEAVES);
        ColorProviderRegistry.ITEM.register(new DebugColorProvider(),RegisterItems.CRACKED_ORES);
    }
}
