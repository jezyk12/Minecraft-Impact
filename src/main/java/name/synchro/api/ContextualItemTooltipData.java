package name.synchro.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * @apiNote An {@link Item} should implement this if necessary. An extended method is provided for getting tooltip data with context.
 * <p>{@link  ContextualItemTooltipData#getTooltipDataConditionally(ItemStack, TooltipContext, MinecraftClient)}
 *  should be called instead of {@link Item#getTooltipData(ItemStack)}</p>
 */
public interface ContextualItemTooltipData {
    Optional<TooltipData> getTooltipDataConditionally(ItemStack stack, TooltipContext context, MinecraftClient client);
}
