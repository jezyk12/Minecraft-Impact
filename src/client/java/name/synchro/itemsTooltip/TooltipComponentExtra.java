package name.synchro.itemsTooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;

public interface TooltipComponentExtra extends TooltipComponent {
    default MinecraftClient client(){
        return MinecraftClient.getInstance();
    }

    default boolean creative(){
        ClientPlayerEntity player = client().player;
        if (player != null) {
            return player.isCreative();
        }
        else return false;
    }

    default boolean advanced(){
        return client().options.advancedItemTooltips;
    }
}
