package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.api.ItemSpeciallyCombinable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow public abstract ItemStack getCursorStack();

    @Unique boolean insertItemReturn = false;

    @WrapOperation(method = "internalOnSlotClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", ordinal = 0))
    private boolean warpCanCombine(ItemStack stack, ItemStack otherStack, Operation<Boolean> original){
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            return specialItem.canCombineToExistingStack(stack, otherStack);
        }
        return original.call(stack, otherStack);
    }

    @WrapOperation(method = "insertItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", ordinal = 0))
    private boolean modifyInsertItem(ItemStack stack, ItemStack otherStack, Operation<Boolean> original,
                                     @Local Slot slot){
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            if (slot.canInsert(stack) && specialItem.canCombineToExistingStack(stack, otherStack)) {
                specialItem.combineToExistingStack(stack, otherStack);
                slot.markDirty();
                this.insertItemReturn = true;
            }
            return false;
        }
        return original.call(stack, otherStack);
    }

    @Inject(method = "insertItem", at = @At("RETURN"), cancellable = true)
    private void modifyInsertItemReturn(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(this.insertItemReturn);
        cir.cancel();
    }

    @Inject(method = "canInsertItemIntoSlot", at = @At("HEAD"), cancellable = true)
    private static void modifyCanInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow, CallbackInfoReturnable<Boolean> cir){
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem && slot != null) {
            if (slot.canInsert(stack) && specialItem.canCombineToExistingStack(stack, slot.getStack())) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @WrapOperation(method = "internalOnSlotClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;takeStackRange(IILnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", ordinal = 1))
    private ItemStack warpDoubleClickOperation(Slot instance, int min, int max, PlayerEntity player, Operation<ItemStack> original){
        ItemStack cursorStack = this.getCursorStack();
        if (cursorStack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            ItemStack slotStack = instance.getStack();
            if (specialItem.canCombineToExistingStack(slotStack, cursorStack)) {
                specialItem.combineToExistingStack(slotStack, cursorStack);
                instance.markDirty();
            }
            return ItemStack.EMPTY;
        }
        return original.call(instance, min, max, player);
    }
}
