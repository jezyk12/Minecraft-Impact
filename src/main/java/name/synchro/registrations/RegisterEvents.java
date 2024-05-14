package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.items.OresMixture;

public class RegisterEvents {
    public static void registerAll() {
        OresMixture.registerTooltipEvent();
        Synchro.LOGGER.debug("Registered all events for" + Synchro.MOD_ID);
    }
}
