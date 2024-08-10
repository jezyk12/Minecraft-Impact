package name.synchro.mixin.client;

import name.synchro.modUtilData.ModDataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ModDataManager.Provider {

    @Shadow @Final private MinecraftClient client;

    @Override
    public ModDataManager synchro$getModDataManager() {
        return ((ModDataManager.Provider)this.client).synchro$getModDataManager();
    }
}
