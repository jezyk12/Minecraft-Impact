package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.screens.ElectricConsumerScreen;
import name.synchro.screens.ElectricSourceScreen;
import name.synchro.screens.MillstoneScreen;
import name.synchro.screens.UniversalMeterScreen;
import name.synchro.guidance.HandledGuidanceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RegisterScreens {
    public static void registerAll(){
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_LAMP_SCREEN_HANDLER, ElectricConsumerScreen::new);
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_SOURCE_SCREEN_HANDLER, ElectricSourceScreen::new);
        HandledScreens.register(RegisterScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER, UniversalMeterScreen::new);
        HandledScreens.register(RegisterScreenHandlers.GUIDANCE_SCREEN_HANDLER, HandledGuidanceScreen::new);
        HandledScreens.register(RegisterScreenHandlers.MILLSTONE_SCREEN_HANDLER, MillstoneScreen::new);
        Synchro.LOGGER.debug("Registered client screens for" + Synchro.MOD_ID);
    }
}
