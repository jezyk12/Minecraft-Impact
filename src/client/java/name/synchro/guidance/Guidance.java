package name.synchro.guidance;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Guidance {
    private static final KeyBinding keyBinding =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("name.synchro.guidance",
                    GLFW.GLFW_KEY_GRAVE_ACCENT, "category.synchro.synchro"));

    public static void setupKeyBinding(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new GuidanceMainScreen());
                    //openGuidanceScreen();
                }
            }
        });
    }

//    @Deprecated
//    public static void openGuidanceScreen() {
//        MinecraftServer server = MinecraftClient.getInstance().getServer();
//        ClientPlayerEntity  clientPlayer = MinecraftClient.getInstance().player;
//        ServerPlayerEntity serverPlayer = null;
//        if (server != null) {
//            if (clientPlayer != null) {
//                serverPlayer = server.getPlayerManager().getPlayer(clientPlayer.getUuid());
//            }
//        }
//        if (serverPlayer != null) {
//            serverPlayer.openHandledScreen(new GuidanceFactory());
//        }
//    }

}
