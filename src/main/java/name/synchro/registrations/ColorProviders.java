package name.synchro.registrations;

import name.synchro.colorProviders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

@Environment(EnvType.CLIENT)
public final class ColorProviders {
    public static void registerAll(){
        ColorProviderRegistry.ITEM.register(new RawMixedOreColorProvider(), ModItems.RAW_MIXED_ORE);
        ColorProviderRegistry.BLOCK.register(new BushWithFlowersColorProvider(), ModBlocks.BUSH_WITH_FLOWERS);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), ModBlocks.BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), ModBlocks.TOMATO_BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), ModBlocks.BANANA_STEM);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), ModBlocks.BANANA_LEAVES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ModItems.LUMP_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ModItems.CRACKED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ModItems.CRUSHED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ModItems.ORES_DUST);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_COARSE);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_CRACKED);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_DARK);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_LIGHT);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_SCRATCH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_SMOOTH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_SHINY);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), ModBlocks.ROCK_STRATIFORM);
    }
}
