package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class RegisterEventsServer {
    public static final Identifier LISTENER_ID = Identifier.of(Synchro.MOD_ID, "synchro_data_listener");
    public static void registerAll() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return LISTENER_ID;
            }

            @Override
            public void reload(ResourceManager manager) {
                FluidReaction.onReload();
                CowFeedDataEntry.onReload();
            }
        });
        Synchro.LOGGER.debug("Registered all server-side events for" + Synchro.MOD_ID);
    }
}
