package name.synchro.dataGeneration;

import name.synchro.api.ModModelDataProvider;
import name.synchro.blocks.SlopeBlock;
import name.synchro.registrations.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModelsData extends ModModelDataProvider {

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
        blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.BURNT_CHARCOAL_BLOCK)
                .stairs(ModBlocks.BURNT_CHARCOAL_STAIRS)
                .slab(ModBlocks.BURNT_CHARCOAL_SLAB)
                .fence(ModBlocks.BURNT_CHARCOAL_FENCE)
                .pressurePlate(ModBlocks.BURNT_CHARCOAL_PRESSURE_PLATE);
        SlopeBlock.SLOPE_BLOCKS.forEach(block -> registerSlope(blockStateModelGenerator, block));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

}
