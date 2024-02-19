package name.synchro.guidance;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;

public class OpenGuidance {
    private static final KeyBinding keyBinding =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("name.synchro.guidance",
                    GLFW.GLFW_KEY_GRAVE_ACCENT, "category.synchro.synchro"));
    public static void setupKeyBinding(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.player != null) {
                    openGuidanceScreen();
                }
            }
        });
    }

    public static void openGuidanceScreen() {
        MinecraftServer server = MinecraftClient.getInstance().getServer();
        ClientPlayerEntity  clientPlayer = MinecraftClient.getInstance().player;
        ServerPlayerEntity serverPlayer = null;
        if (server != null) {
            if (clientPlayer != null) {
                serverPlayer = server.getPlayerManager().getPlayer(clientPlayer.getUuid());
            }
        }
        if (serverPlayer != null) {
            serverPlayer.openHandledScreen(new Guidance());
        }
    }

}
