package name.synchro.registrations;

import name.synchro.colorProviders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

@Environment(EnvType.CLIENT)
public class RegisterColorProviders {
    public static void registerAll(){
        ColorProviderRegistry.ITEM.register(new RawMixedOreColorProvider(), RegisterItems.RAW_MIXED_ORE);
        ColorProviderRegistry.BLOCK.register(new BushWithFlowersColorProvider(), RegisterBlocks.BUSH_WITH_FLOWERS);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), RegisterBlocks.BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), RegisterBlocks.TOMATO_BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), RegisterBlocks.BANANA_STEM);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), RegisterBlocks.BANANA_LEAVES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), RegisterItems.LUMP_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), RegisterItems.CRACKED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), RegisterItems.CRUSHED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), RegisterItems.ORES_DUST);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_COARSE);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_CRACKED);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_DARK);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_LIGHT);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_SCRATCH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_SMOOTH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_SHINY);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), RegisterBlocks.ROCK_STRATIFORM);
    }
}
