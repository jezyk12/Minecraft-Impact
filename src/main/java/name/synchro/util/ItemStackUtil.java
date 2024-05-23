package name.synchro.util;

import name.synchro.api.ItemSpeciallyCombinable;
import net.minecraft.item.ItemStack;

public final class ItemStackUtil {
    public static boolean canMerge(ItemStack givenStack, ItemStack existingStack) {
        if (givenStack.isEmpty() || existingStack.isEmpty()) {
            return true;
        }
        if (givenStack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            return specialItem.canCombineToExistingStack(givenStack, existingStack);
        }
        return ItemStack.canCombine(givenStack, existingStack);
    }

    public static boolean canCompletelyMerge(ItemStack givenStack, ItemStack existingStack) {
        if (givenStack.isEmpty() || existingStack.isEmpty()) {
            return true;
        }
        if (givenStack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            return specialItem.canCompletelyCombineToExistingStack(givenStack, existingStack);
        }
        return ItemStack.canCombine(givenStack, existingStack) && givenStack.getCount() + existingStack.getCount() <= givenStack.getMaxCount();
    }

    public static Result transfer(ItemStack givenStack, ItemStack existingStack) {
        ItemStack stack1 = givenStack.copy();
        ItemStack stack2 = existingStack.copy();
        if (!stack1.isEmpty()) {
            if (stack2.isEmpty()) {
                return new Result(ItemStack.EMPTY, stack1);
            }
            if (stack1.getItem() instanceof ItemSpeciallyCombinable specialItem) {
                if (specialItem.canCombineToExistingStack(stack1, stack2)) {
                    stack1 = specialItem.combineToExistingStack(stack1, stack2);
                }
                return new Result(stack1, stack2);
            }
            if (ItemStack.canCombine(stack1, stack2)) {
                if (stack1.getCount() + stack2.getCount() <= stack1.getMaxCount()) {
                    stack2.increment(stack1.getCount());
                    return new Result(ItemStack.EMPTY, stack2);
                } else {
                    int count = stack1.getCount() + stack2.getCount() - stack1.getMaxCount();
                    stack2.setCount(stack2.getMaxCount());
                    stack1.setCount(count);
                    return new Result(stack1, stack2);
                }
            }
        }
        return new Result(stack1, stack2);
    }

    public record Result(ItemStack givenStackAfter, ItemStack existingStackAfter) {}
}
