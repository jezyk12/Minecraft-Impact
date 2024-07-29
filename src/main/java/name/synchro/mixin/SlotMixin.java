package name.synchro.mixin;

import name.synchro.util.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void insertStackModify(ItemStack stack, int count, CallbackInfoReturnable<ItemStack> cir){
        ItemStackUtil.Combinable.onSlotInsert((Slot) (Object) this, stack, cir);
    }
}
