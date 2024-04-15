package name.synchro;

import name.synchro.blockModels.SynchroModelLoadingPlugin;
import name.synchro.guidance.Guidance;
import name.synchro.registrations.*;
import name.synchro.synchroBlocks.GasRenderHandler;
import name.synchro.worldRendering.DebugRendering;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class SynchroClient implements ClientModInitializer {
    public static final Identifier MOD_ICONS = new Identifier(Synchro.MOD_ID, "textures/gui/mod_icons.png");
    public static boolean applyNewHud = true;
    public static boolean displayExtraCollisions = false;
    public static float debugNum0 = 0.0f;
    public static float debugNum1 = 1.0f;
    public static float debugNum2 = 1.0f;
    public static float debugNum3 = 1.0f;
    public static final ArrayList<Float> debugNumbers = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        for (int i = 0; i < 16; ++i){
            debugNumbers.add(1.0f);
        }
        DebugRendering.renderAll();
        RegisterBlockEntityRenderers.registerAll();
        RegisterColorProviders.registerAll();
        ModelLoadingPlugin.register(new SynchroModelLoadingPlugin());
        RegisterClientNetworking.registerAll();
        SetupRenderLayer.setupAll();
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.WATER_VAPOR_GAS, new GasRenderHandler(0xff0000));
        Guidance.setupKeyBinding();
        RegisterClientScreens.registerAll();
        RegisterEntityRendering.registerAll();
        Synchro.LOGGER.info("Mod Synchro has been initialized in client.");
    }
}
