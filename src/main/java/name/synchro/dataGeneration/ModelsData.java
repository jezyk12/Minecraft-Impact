package name.synchro.dataGeneration;

import name.synchro.registrations.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModelsData extends FabricModelProvider {
    public ModelsData(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PLANT_FIBRE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SEKITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.AOITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MIDORITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MURAXKITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.GUMITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NGANITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.HAAKITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BAAKITE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
