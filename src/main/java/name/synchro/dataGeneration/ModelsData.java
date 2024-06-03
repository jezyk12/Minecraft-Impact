package name.synchro.dataGeneration;

import name.synchro.registrations.BlocksRegistered;
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
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.PLANT_FIBRE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.SEKITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.AOITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.MIDORITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.MURAXKITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.GUMITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.NGANITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.HAAKITE);
        blockStateModelGenerator.registerSimpleCubeAll(BlocksRegistered.BAAKITE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
