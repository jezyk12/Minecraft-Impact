package name.synchro.colorProviders;

import name.synchro.SynchroClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;

@Deprecated
@Environment(EnvType.CLIENT)
public class DebugColorProvider implements ItemColorProvider {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if ( -1 < tintIndex && tintIndex < 8) return (int) (double) SynchroClient.debugNumbers.get(tintIndex);
        return -1;
    }
}
