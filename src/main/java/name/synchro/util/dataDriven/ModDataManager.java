package name.synchro.util.dataDriven;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import name.synchro.Synchro;
import name.synchro.registrations.NetworkingIDs;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class ModDataManager {
    final Map<Identifier, ModDataContainer<?>> contents;

    private ModDataManager() {
        this.contents = init();
    }

    private Map<Identifier, ModDataContainer<?>> init(){
        return ImmutableMap.<Identifier, ModDataContainer<?>>builder()
                .put(CowWorkingFeedsData.ID, new CowWorkingFeedsData())
                .build();
    }

    public Map<Identifier, ModDataContainer<?>> getContents() {
        return contents;
    }

    public static ModDataManager get(World world){
        if (world.isClient()){
            MinecraftClient client = MinecraftClient.getInstance();
            return  ((Provider) client).synchro$getModDataManager();
        }
        else if (world.getServer() != null){
            return  ((Provider) world.getServer()).synchro$getModDataManager();
        }
        else {
            Synchro.LOGGER.error("Unexpected accessing to ModDataManager from world: {}", world.asString());
            return new ModDataManager(){};
        }
    }

    public void apply(PacketByteBuf buf){}

    public void apply(Map<Identifier, JsonElement> data){}

    public void send(MinecraftServer server){}

    public void send(MinecraftServer server, ServerPlayerEntity player){}

    public static class ForServer extends ModDataManager {
        @Override
        public void apply(Map<Identifier, JsonElement> data){
            data.forEach(((identifier, jsonElement) -> {
                ModDataContainer<?> container = this.contents.get(identifier);
                if (container != null) {
                    container.deserialize(jsonElement);
                }
            }));
        }

        @Override
        public void send(MinecraftServer server){
            PacketByteBuf buf = createBuf();
            server.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                ServerPlayNetworking.send(serverPlayerEntity, NetworkingIDs.MOD_DATA_PACK_ID, buf);
            });
        }

        @Override
        public void send(MinecraftServer server, ServerPlayerEntity player) {
            PacketByteBuf buf = createBuf();
            ServerPlayNetworking.send(player, NetworkingIDs.MOD_DATA_PACK_ID, buf);
        }

        private @NotNull PacketByteBuf createBuf() {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.contents.size());
            this.contents.forEach((identifier, modDataContainer) -> {
                buf.writeIdentifier(identifier);
                modDataContainer.writeBuf(buf);
            });
            return buf;
        }
    }

    public static class ForClient extends ModDataManager {
        public static void receiveData(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
            if (client instanceof Provider provider){
                provider.synchro$getModDataManager().apply(buf);
            }
        };

        @Override
        public void apply(PacketByteBuf buf){
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier identifier = buf.readIdentifier();
                ModDataContainer<?> container = this.contents.get(identifier);
                if (container != null) {
                    container.readBuf(buf);
                }
                else {
                    Synchro.LOGGER.error("Unable to receive mod data pack from server: Missing {}", identifier);
                    break;
                }
            }
        }
    }

    public interface Provider {
        ModDataManager synchro$getModDataManager();
    }
}
