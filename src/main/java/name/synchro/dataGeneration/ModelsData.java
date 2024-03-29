package name.synchro.dataGeneration;

import name.synchro.registrations.RegisterBlocks;
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
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.PLANT_FIBRE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.SEKITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.AOITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.MIDORITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.MURAXKITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.GUMITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.NGANITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.HAAKITE);
        blockStateModelGenerator.registerSimpleCubeAll(RegisterBlocks.BAAKITE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
