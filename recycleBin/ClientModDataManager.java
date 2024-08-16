package name.synchro.util;

import name.synchro.Synchro;
import name.synchro.playNetworking.ModDataPayload;
import name.synchro.modUtilData.ModDataContainer;
import name.synchro.modUtilData.ModDataManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

public class ClientModDataManager extends ModDataManager {
    public static void receiveData(ModDataPayload payload, ClientPlayNetworking.Context context) {
        if (context.client() instanceof Provider provider) {
            provider.synchro$getModDataManager().apply(payload.buf(), context.player().getRegistryManager());
        }
    }

    @Override
    public void apply(PacketByteBuf buf, DynamicRegistryManager dynamicRegistryManager) {
        int size = buf.readInt();
        RegistryByteBuf registryByteBuf = new RegistryByteBuf(buf, dynamicRegistryManager);
        for (int i = 0; i < size; i++) {
            Identifier identifier = buf.readIdentifier();
            ModDataContainer<?> container = this.getContents().get(identifier);
            if (container != null) {
                container.readBuf(registryByteBuf);
            } else {
                Synchro.LOGGER.error("Unable to receive mod data pack from server: Missing {}", identifier);
                break;
            }
        }
    }
}
