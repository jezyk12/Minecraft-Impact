package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

public class RegisterClientNetworking {
    public static final Identifier HUNGER_DATA_PACKET_ID = new Identifier(Synchro.MOD_ID, "exhaustion_packet");
    public static void registerAll(){
        ClientPlayNetworking.registerGlobalReceiver(HUNGER_DATA_PACKET_ID, (client, handler, buf, responseSender) -> {
            float saturation = buf.readFloat();
            float exhaustion = buf.readFloat();
            client.execute(() -> {
                ClientPlayerEntity clientPlayer = client.player;
                if (clientPlayer != null) {
                    clientPlayer.getHungerManager().setSaturationLevel(saturation);
                    clientPlayer.getHungerManager().setExhaustion(exhaustion);
                }
            });
        });
        Synchro.LOGGER.debug("Registered client networking for "+Synchro.MOD_ID);
    }
}
