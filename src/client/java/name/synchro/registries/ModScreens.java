package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.registrations.ModScreenHandlers;
import name.synchro.screens.ElectricConsumerScreen;
import name.synchro.screens.ElectricSourceScreen;
import name.synchro.screens.MillstoneScreen;
import name.synchro.screens.UniversalMeterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public final class ModScreens {
    public static void registerAll(){
        HandledScreens.register(ModScreenHandlers.ELECTRIC_LAMP_SCREEN_HANDLER, ElectricConsumerScreen::new);
        HandledScreens.register(ModScreenHandlers.ELECTRIC_SOURCE_SCREEN_HANDLER, ElectricSourceScreen::new);
        HandledScreens.register(ModScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER, UniversalMeterScreen::new);
        //HandledScreens.register(ModScreenHandlers.GUIDANCE_SCREEN_HANDLER, HandledGuidanceScreen::new);
        HandledScreens.register(ModScreenHandlers.MILLSTONE_SCREEN_HANDLER, MillstoneScreen::new);
        Synchro.LOGGER.debug("Registered client screens for" + Synchro.MOD_ID);
    }
}
