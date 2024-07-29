package name.synchro.mixin.client.hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.registrations.ModItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @WrapOperation(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;",
                    ordinal = 0))
    private UseAction warpItemAnimation(ItemStack item, Operation<UseAction> original,
                                        @Local(argsOnly = true) MatrixStack matrices){
        if (item.isOf(ModItems.SNOWBALL_LAUNCHER)){
            matrices.translate(-0.5f, 0.05f ,0.0f);
            return UseAction.NONE;
        }
        return original.call(item);
    }
}
