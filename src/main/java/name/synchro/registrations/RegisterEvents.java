package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.items.OresMixture;
import name.synchro.util.NotSupported;

public final class RegisterEvents {
    public static void registerAll() {
        OresMixture.registerTooltipEvent();
        NotSupported.registerTooltip();
        Synchro.LOGGER.debug("Registered all events for" + Synchro.MOD_ID);
    }
}
