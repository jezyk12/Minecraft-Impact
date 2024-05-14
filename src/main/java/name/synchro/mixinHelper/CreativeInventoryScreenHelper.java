package name.synchro.mixinHelper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import name.synchro.api.ContextualItemTooltipData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class CreativeInventoryScreenHelper {
    public static Optional<TooltipData> getTooltipData(MinecraftClient client, ItemStack stack, Operation<Optional<TooltipData>> original) {
        if (stack.getItem() instanceof ContextualItemTooltipData item) {
            boolean advanced = false;
            boolean creative = true;
            if (client != null) {
                advanced = client.options.advancedItemTooltips;
            }
            return item.getTooltipDataConditionally(stack, new TooltipContext.Default(advanced, creative), client);
        }
        return original.call(stack);
    }
}
