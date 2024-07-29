package name.synchro.mixin;

import name.synchro.util.dataDriven.ModDataManager;
import name.synchro.util.dataDriven.ServerModDataManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ModDataManager.Provider {
    @Unique @Final
    private final ModDataManager modDataManager = new ServerModDataManager();

    @Override
    public ModDataManager synchro$getModDataManager() {
        return this.modDataManager;
    }
}
