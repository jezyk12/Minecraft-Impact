package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.api.ContextualItemTooltipData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @WrapOperation(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;"))
    private Optional<TooltipData> getTooltipDataContextually(ItemStack stack, Operation<Optional<TooltipData>> original) {
        if (stack.getItem() instanceof ContextualItemTooltipData item) {
            boolean advanced = false;
            boolean creative = false;
            if (this.client != null) {
                advanced = this.client.options.advancedItemTooltips;
                if (this.client.player != null) {
                    creative = this.client.player.isCreative();
                }
            }
            return item.getTooltipDataConditionally(stack, new TooltipContext.Default(advanced, creative), client);
        }
        return original.call(stack);
    }
}
