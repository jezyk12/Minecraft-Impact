package name.synchro.util;

import name.synchro.mixinHelper.PlayerFireTickSync;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

public final class NewHudUtil {
    public static void receiveHungerDataPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        float saturation = buf.readFloat();
        float exhaustion = buf.readFloat();
        client.execute(() -> {
            ClientPlayerEntity clientPlayer = client.player;
            if (clientPlayer != null) {
                clientPlayer.getHungerManager().setSaturationLevel(saturation);
                clientPlayer.getHungerManager().setExhaustion(exhaustion);
            }
        });
    }

    public static void receiveFireTicksDataPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int fireTicks = buf.readInt();
        client.execute(() -> {
            ClientPlayerEntity clientPlayer = client.player;
            if (clientPlayer instanceof PlayerFireTickSync) {
                ((PlayerFireTickSync) clientPlayer).setTheFireTicks(fireTicks);
            }
        });
    }
}
