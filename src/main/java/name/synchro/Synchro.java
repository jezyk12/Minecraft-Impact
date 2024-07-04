package name.synchro;

import name.synchro.networkLink.networkAlgorithm.NetworkSearchHandler;
import name.synchro.registrations.*;
import name.synchro.util.IrregularVoxelShapes;
import name.synchro.util.dataDriven.ModDataLoader;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Synchro implements ModInitializer {
	public static final String MOD_ID = "synchro";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModDataLoader MOD_DATA_LOADER = ModDataLoader.getInstance();


	@Override
	public void onInitialize() {
		SynchroStandardStatic.initialAll();
		NetworkSearchHandler.loadNetworkSearchHandler();
		ModFeatures.registerAll();
		ModItems.registerAll();
		ModItemGroups.registerAll();
		ModBlocks.registerAll();
		ModTags.registerAll();
		//MillstoneRecipes.buildAll();
		ModBlockEntities.registerAll();
		ModFluids.registerAll();
		ModScreenHandlers.registerAll();
		ModRecipes.registerAll();
		ModEntities.registerAll();
		IrregularVoxelShapes.addAndLoadAllShapes();
		//RegisterPointsOfInterest.registerAll();
		RegisterEventsServer.registerAll();
		LOGGER.info("Mod Synchro has been initialized.");
	}
}