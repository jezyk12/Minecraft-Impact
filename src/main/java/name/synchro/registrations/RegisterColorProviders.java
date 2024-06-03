package name.synchro.registrations;

import name.synchro.colorProviders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

@Environment(EnvType.CLIENT)
public final class RegisterColorProviders {
    public static void registerAll(){
        ColorProviderRegistry.ITEM.register(new RawMixedOreColorProvider(), ItemsRegistered.RAW_MIXED_ORE);
        ColorProviderRegistry.BLOCK.register(new BushWithFlowersColorProvider(), BlocksRegistered.BUSH_WITH_FLOWERS);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), BlocksRegistered.BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), BlocksRegistered.TOMATO_BUSH);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), BlocksRegistered.BANANA_STEM);
        ColorProviderRegistry.BLOCK.register(new VanillaGrassColorProvider(), BlocksRegistered.BANANA_LEAVES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ItemsRegistered.LUMP_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ItemsRegistered.CRACKED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ItemsRegistered.CRUSHED_ORES);
        ColorProviderRegistry.ITEM.register(new OresMixtureColorProvider(), ItemsRegistered.ORES_DUST);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_COARSE);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_CRACKED);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_DARK);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_LIGHT);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_SCRATCH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_SMOOTH);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_SHINY);
        ColorProviderRegistry.BLOCK.register(new RockColorProvider(), BlocksRegistered.ROCK_STRATIFORM);
    }
}
