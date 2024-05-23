package name.synchro.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

/**
 * @apiNote An {@link ItemStack} of {@link Item} implementing this interface
 * can be automatically combined with other ItemStack in custom ways.
 * <p>{@link Item#onStackClicked(ItemStack, Slot, ClickType, PlayerEntity)} as well {@link Item#onClicked(ItemStack, ItemStack, Slot, ClickType, PlayerEntity, StackReference)}
 * can also be overrode for full features while implementing this interface.</p>
 */
public interface ItemSpeciallyCombinable {
    boolean canCombineToExistingStack(ItemStack givenStack, ItemStack existingStack);

    /**
     * @return The remaining stack of givenStack after combining.
     */
    ItemStack combineToExistingStack(ItemStack givenStack, ItemStack existingStack);

    default boolean canCompletelyCombineToExistingStack(ItemStack givenStack, ItemStack existingStack) {
        if (canCombineToExistingStack(givenStack, existingStack)){
            ItemStack stack1 = givenStack.copy();
            ItemStack stack2 = existingStack.copy();
            return combineToExistingStack(stack1, stack2).isEmpty();
        }
        return false;
    }
}
