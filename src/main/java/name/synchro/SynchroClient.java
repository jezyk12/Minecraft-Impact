package name.synchro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.mojang.text2speech.Narrator.LOGGER;

@Environment(EnvType.CLIENT)
public class SynchroClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Mod Synchro has been initialized in client.");
    }
}
