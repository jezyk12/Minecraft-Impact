package name.synchro.util.dataDriven;

import com.google.gson.JsonElement;
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

public class ServerModDataManager extends ModDataManager {
    @Override
    public void apply(Map<Identifier, JsonElement> data) {
        data.forEach(((identifier, jsonElement) -> {
            ModDataContainer<?> container = this.contents.get(identifier);
            if (container != null) {
                container.deserialize(jsonElement);
            }
        }));
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
