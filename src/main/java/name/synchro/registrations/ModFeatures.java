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

public final class ModFeatures {
    public static final RegistryKey<PlacedFeature> BANANA_TREE_FEATURE =
            addPlacedFeature("banana_tree", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION);
    public static final RegistryKey<PlacedFeature> SEKITE_FEATURE =
            addPlacedFeature("sekite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> AOITE_FEATURE =
            addPlacedFeature("aoite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> MIDORITE_FEATURE =
            addPlacedFeature("midorite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> MURAXKITE_FEATURE =
            addPlacedFeature("muraxkite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> GUMITE_FEATURE =
            addPlacedFeature("gumite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> NGANITE_FEATURE =
            addPlacedFeature("nganite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> HAAKITE_FEATURE =
            addPlacedFeature("haakite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
    public static final RegistryKey<PlacedFeature> BAAKITE_FEATURE =
            addPlacedFeature("baakite", BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);


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
