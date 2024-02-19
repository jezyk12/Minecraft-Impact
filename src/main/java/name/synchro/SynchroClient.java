package name.synchro;

import name.synchro.guidance.OpenGuidance;
import name.synchro.registrations.RegisterClientNetworking;
import name.synchro.registrations.RegisterClientScreens;
import name.synchro.registrations.RegisterFluids;
import name.synchro.registrations.SetupRenderLayer;
import name.synchro.synchroBlocks.GasRenderHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

import static com.mojang.text2speech.Narrator.LOGGER;

@Environment(EnvType.CLIENT)
public class SynchroClient implements ClientModInitializer {
    public static boolean applyNewHud = true;

    @Override
    public void onInitializeClient() {
        RegisterClientNetworking.registerAll();
        SetupRenderLayer.setupAll();
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.WATER_VAPOR_GAS, new GasRenderHandler(0xff0000));
        OpenGuidance.setupKeyBinding();
        RegisterClientScreens.registerAll();
        LOGGER.info("Mod Synchro has been initialized in client.");
    }
}
