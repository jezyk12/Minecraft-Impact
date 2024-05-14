package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.mixinHelper.CreativeInventoryScreenHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends Screen {
    protected CreativeInventoryScreenMixin(Text title) {
        super(title);
    }

    @WrapOperation(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;"))
    private Optional<TooltipData> getTooltipDataContextually(ItemStack stack, Operation<Optional<TooltipData>> original) {
        return CreativeInventoryScreenHelper.getTooltipData(this.client, stack, original);
    }

}
