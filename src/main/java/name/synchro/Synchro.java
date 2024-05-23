package name.synchro;

import name.synchro.networkLink.networkAlgorithm.NetworkSearchHandler;
import name.synchro.registrations.*;
import name.synchro.specialRecipes.MillstoneRecipes;
import name.synchro.util.IrregularVoxelShapes;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Synchro implements ModInitializer {
	public static final String MOD_ID = "synchro";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		SynchroStandardStatic.initialAll();
		NetworkSearchHandler.loadNetworkSearchHandler();
		RegisterFeatures.registerAll();
		RegisterItems.registerAll();
		RegisterItemGroups.registerAll();
		RegisterBlocks.registerAll();
		MillstoneRecipes.buildAll();
		RegisterBlockEntities.registerAll();
		RegisterFluids.registerAll();
		RegisterScreenHandlers.registerAll();
		RegisterRecipes.registerAll();
		RegisterEntities.registerAll();
		RegisterEvents.registerAll();
		IrregularVoxelShapes.addAndLoadAllShapes();
		LOGGER.info("Mod Synchro has been initialized.");
	}
}