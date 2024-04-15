package name.synchro;

import name.synchro.clientScreens.ElectricSourceScreen;
import name.synchro.clientScreens.UniversalMeterScreen;
import name.synchro.registrations.RegisterBlocks;
import name.synchro.registrations.RegisterScreenHandlers;
import name.synchro.clientScreens.ElectricConsumerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

import static com.mojang.text2speech.Narrator.LOGGER;

@Environment(EnvType.CLIENT)
public class SynchroClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.CABLE, RenderLayer.getTranslucent());
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_LAMP_SCREEN_HANDLER, ElectricConsumerScreen::new);
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_SOURCE_SCREEN_HANDLER, ElectricSourceScreen::new);
        HandledScreens.register(RegisterScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER, UniversalMeterScreen::new);
        LOGGER.info("Mod Synchro has been initialized in client.");

    }
}
