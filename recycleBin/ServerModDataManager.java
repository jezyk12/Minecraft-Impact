package name.synchro.modUtilData;

import com.google.gson.JsonElement;
import name.synchro.Synchro;
import name.synchro.playNetworking.ModDataPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@Deprecated
public class ServerModDataManager extends ModDataManager {
    @Override
    public void apply(Map<Identifier, JsonElement> data) {
        data.forEach(this::applyToSuitableDataContainer);
    }

    private void applyToSuitableDataContainer(Identifier fullPathId, JsonElement jsonElement){
        String path = fullPathId.getPath();
        try{
            String fileName;
            ModDataContainer<?> container;
            if (path.contains("/")) {
                String[] ids = path.split("/");
                String modDataRoot = ids[0];
                if (!modDataRoot.equals(ModDataLoader.DICTIONARY_ROOT))
                    throw new IllegalArgumentException(fullPathId + "is not data for " + Synchro.MOD_ID);
                String folder = ids[1].split("\\.")[0];
                fileName = ids[ids.length - 1];
                container = this.getContents().get(Identifier.of(Synchro.MOD_ID, folder));
                Objects.requireNonNull(container, "Cannot identify folder: " + folder);
            } else {
                fileName = path;
                container = this.getContents().get(Identifier.of(Synchro.MOD_ID, path));
                Objects.requireNonNull(container, "Cannot identify file: " + path);
            }
            container.deserialize(jsonElement, Identifier.of(Synchro.MOD_ID, fileName));
        }
        catch (Exception e){
            Synchro.LOGGER.warn("Skipped loading {}, due to {}", fullPathId, e);
        }
    }

    @Override
    public void send(MinecraftServer server) {
        ModDataPayload payload = createPayload(server.getRegistryManager());
        server.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
            ServerPlayNetworking.send(serverPlayerEntity, payload);
        });
    }

    @Override
    public void send(MinecraftServer server, ServerPlayerEntity player) {
        ModDataPayload payload = createPayload(server.getRegistryManager());
        ServerPlayNetworking.send(player, payload);
    }

    private @NotNull RegistryByteBuf createBuf(DynamicRegistryManager dynamicRegistryManager) {
        RegistryByteBuf buf = new RegistryByteBuf(PacketByteBufs.create(), dynamicRegistryManager);
        buf.writeInt(this.contents.size());
        this.contents.forEach((identifier, modDataContainer) -> {
            buf.writeIdentifier(identifier);
            modDataContainer.writeBuf(buf);
        });
        return buf;
    }

    private ModDataPayload createPayload(DynamicRegistryManager dynamicRegistryManager){
        RegistryByteBuf buf = createBuf(dynamicRegistryManager);
        return new ModDataPayload(buf);
    }
}
