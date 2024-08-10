package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.modUtilData.ModDataLoader;
import name.synchro.modUtilData.ModDataManager;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
                ModDataLoader.getInstance().reload(manager);
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (server instanceof ModDataManager.Provider provider) {
                ModDataManager modDataManager = provider.synchro$getModDataManager();
                modDataManager.apply(ModDataLoader.getInstance().getContents());
            }
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (server instanceof ModDataManager.Provider provider){
                provider.synchro$getModDataManager().apply(ModDataLoader.getInstance().getContents());
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (player.getServer() instanceof ModDataManager.Provider provider){
                provider.synchro$getModDataManager().send(player.getServer(), player);
            }
        });
        Synchro.LOGGER.debug("Registered all server-side events for" + Synchro.MOD_ID);
    }
}
