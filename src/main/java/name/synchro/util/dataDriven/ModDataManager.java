package name.synchro.util.dataDriven;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import name.synchro.Synchro;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class ModDataManager {
    public static final ModDataManager DUMMY = new ModDataManager(){};
    final Map<Identifier, ModDataContainer<?>> contents;

    protected ModDataManager() {
        this.contents = init();
    }

    private Map<Identifier, ModDataContainer<?>> init() {
        return ImmutableMap.<Identifier, ModDataContainer<?>>builder()
                .put(CowWorkingFeedsData.ID, new CowWorkingFeedsData())
                .build();
    }

    public Map<Identifier, ModDataContainer<?>> getContents() {
        return contents;
    }

    public void apply(PacketByteBuf buf, DynamicRegistryManager dynamicRegistryManager) {
        warn("apply(PacketByteBuf, DynamicRegistryManager)");
    }

    public void apply(Map<Identifier, JsonElement> data) {
        warn("apply(Map<Identifier, JsonElement>)");
    }

    public void send(MinecraftServer server) {
        warn("send(MinecraftServer)");
    }

    public void send(MinecraftServer server, ServerPlayerEntity player) {
        warn("send(MinecraftServer, ServerPlayerEntity)");
    }

    private void warn(String method) {
        Synchro.LOGGER.warn("Dummy method: {} in ModDataManager was called unexpectedly.", method);
    }

    public interface Provider {
        ModDataManager synchro$getModDataManager();
    }
}
