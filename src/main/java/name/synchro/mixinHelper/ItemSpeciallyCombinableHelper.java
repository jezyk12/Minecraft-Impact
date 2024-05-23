package name.synchro.mixinHelper;

import name.synchro.api.ItemSpeciallyCombinable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ItemSpeciallyCombinableHelper {
    public static void onPlayerStackInsert(PlayerInventory playerInventory, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            ItemStack originalStack = stack.copy();
            if (slot < 0) {
                while (!stack.isEmpty()){
                    int searchedSlot = playerInventory.getOccupiedSlotWithRoomForStack(stack);
                    if (searchedSlot > -1) {
                        ItemStack existingStack = playerInventory.getStack(searchedSlot);
                        stack = specialItem.combineToExistingStack(stack, existingStack);
                        existingStack.setBobbingAnimationTime(5);
                    }
                    else {
                        searchedSlot = playerInventory.getEmptySlot();
                        if (searchedSlot > -1) {
                            ItemStack copy = stack.copy();
                            playerInventory.setStack(searchedSlot, copy);
                            copy.setBobbingAnimationTime(5);
                            stack.setCount(0);
                            cir.setReturnValue(true);
                            cir.cancel();
                            return;
                        }
                        else break;
                    }
                }
            }
            else {
                ItemStack existingStack = playerInventory.getStack(slot);
                stack = specialItem.combineToExistingStack(stack, existingStack);
            }
            if (playerInventory.player.getAbilities().creativeMode) {
                stack.setCount(0);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
            cir.setReturnValue(!originalStack.equals(stack));
            cir.cancel();
        }
    }

    public static void onSimpleInventoryStackTransfer(SimpleInventory instance, ItemStack source, ItemStack target, CallbackInfo ci){
        if (source.getItem() instanceof ItemSpeciallyCombinable specialItem && specialItem.canCombineToExistingStack(source, target)) {
            specialItem.combineToExistingStack(source, target);
            instance.markDirty();
            ci.cancel();
        }
    }

    public static void onSlotInsert(Slot instance, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (instance.canInsert(stack) && stack.getItem() instanceof ItemSpeciallyCombinable specialItem){
            ItemStack slotStack = instance.getStack();
            if (slotStack.isEmpty()) {
                instance.setStack(stack.copy());
                stack.setCount(0);
            }
            else if (specialItem.canCombineToExistingStack(stack, slotStack)) {
                specialItem.canCombineToExistingStack(stack, slotStack);
                instance.setStack(slotStack);
            }
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }
}
