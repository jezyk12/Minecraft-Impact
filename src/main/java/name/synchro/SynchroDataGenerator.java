package name.synchro;

import name.synchro.dataGeneration.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SynchroDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack myPack = fabricDataGenerator.createPack();
		myPack.addProvider(BlockLootTablesData::new);
		myPack.addProvider(RecipesData::new);
		myPack.addProvider(AdvancementsData::new);
		myPack.addProvider(ModelsData::new);
		myPack.addProvider(BlockTagsData::new);
		myPack.addProvider(ItemTagsData::new);
		myPack.addProvider(DefaultCowWorkingFeedsData::new);
		myPack.addProvider(IntrinsicModData::new);
		myPack.addProvider(TranslationENUSData::new);
	}

}
