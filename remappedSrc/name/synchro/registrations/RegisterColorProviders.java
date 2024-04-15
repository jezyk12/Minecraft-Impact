package name.synchro.registrations;

import name.synchro.colorProviders.RawMixedOreColorProvider;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class RegisterColorProviders {
    public static void registerAll(){
        ColorProviderRegistry.ITEM.register(new RawMixedOreColorProvider(),RegisterItems.MIXED_RAW_ORE);
    }
}
