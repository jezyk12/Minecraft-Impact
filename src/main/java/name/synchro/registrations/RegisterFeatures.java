package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.function.Predicate;

public class RegisterFeatures {
    public static final RegistryKey<PlacedFeature> BANANA_TREE_PLACED =
            addPlacedFeature("banana_tree", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION);

    private static RegistryKey<PlacedFeature> addPlacedFeature(String path, Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Feature step){
        RegistryKey<PlacedFeature> key =
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(Synchro.MOD_ID, path));
        BiomeModifications.addFeature(biomeSelector, step, key);
        return key;
    }

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered all features for " + Synchro.MOD_ID);
    }
}
