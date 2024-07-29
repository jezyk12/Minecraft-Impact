package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.itemsTooltip.OresMixtureTooltipComponent;
import name.synchro.util.NotSupported;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

@Environment(EnvType.CLIENT)
public final class RegisterEventsClient {
    public static void registerAll() {
        registerTooltipEvent();
        NotSupported.registerTooltip();
        Synchro.LOGGER.debug("Registered all client-side events for" + Synchro.MOD_ID);
    }

    public static void registerTooltipEvent(){
        TooltipComponentCallback.EVENT.register(OresMixtureTooltipComponent::getComponent);
    }
}
