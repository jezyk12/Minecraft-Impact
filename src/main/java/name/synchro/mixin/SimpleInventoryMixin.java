package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.api.ItemSpeciallyCombinable;
import name.synchro.util.ItemStackUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleInventory.class)
public class SimpleInventoryMixin {
    @Inject(method = "transfer", at = @At("HEAD"), cancellable = true)
    private void transferSpeciallyCombinableStacks(ItemStack source, ItemStack target, CallbackInfo ci) {
        ItemStackUtil.Combinable.onSimpleInventoryStackTransfer((SimpleInventory) (Object) this, source, target, ci);
    }

    @WrapOperation(method = "canInsert", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsAndComponentsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean canInsertWrap(ItemStack stack, ItemStack otherStack, Operation<Boolean> original) {
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
           return specialItem.canCombineToExistingStack(stack, otherStack);
        }
        return original.call(stack, otherStack);
    }

    @WrapOperation(method = "addToExistingSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsAndComponentsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean addToExistingSlotWarp(ItemStack stack, ItemStack otherStack, Operation<Boolean> original) {
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            return specialItem.canCombineToExistingStack(stack, otherStack);
        }
        return original.call(stack, otherStack);
    }
}
