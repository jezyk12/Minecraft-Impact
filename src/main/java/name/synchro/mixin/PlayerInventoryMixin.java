package name.synchro.mixin;

import name.synchro.api.ItemSpeciallyCombinable;
import name.synchro.util.ItemStackUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void speciallyCombineStacks(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStackUtil.Combinable.onPlayerStackInsert((PlayerInventory) (Object) this, slot, stack, cir);
    }

    @Inject(method = "canStackAddMore", at = @At("HEAD"), cancellable = true)
    private void canStackAddMore(ItemStack existingStack, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            cir.setReturnValue(specialItem.canCombineToExistingStack(stack, existingStack));
            cir.cancel();
        }
    }

}
