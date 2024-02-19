package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.clientScreens.ElectricConsumerScreen;
import name.synchro.clientScreens.ElectricSourceScreen;
import name.synchro.clientScreens.UniversalMeterScreen;
import name.synchro.guidance.GuidanceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RegisterClientScreens {
    public static void registerAll(){
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_LAMP_SCREEN_HANDLER, ElectricConsumerScreen::new);
        HandledScreens.register(RegisterScreenHandlers.ELECTRIC_SOURCE_SCREEN_HANDLER, ElectricSourceScreen::new);
        HandledScreens.register(RegisterScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER, UniversalMeterScreen::new);
        HandledScreens.register(RegisterScreenHandlers.GUIDANCE_SCREEN_HANDLER, GuidanceScreen::new);
        Synchro.LOGGER.debug("Registered client screens for" + Synchro.MOD_ID);
    }
}
