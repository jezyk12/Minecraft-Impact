package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.fluids.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldView.class)
public interface WorldViewMixin {
    @WrapOperation(method = "containsFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixContainsFluid(BlockState instance, Operation<FluidState> original,
                                        @Local BlockPos.Mutable pos) {
        if (this instanceof FluidHelper.ForRenderedChunk getter){
            return getter.synchro$getFluidState(pos);
        }
        else {
            return original.call(instance);
        }
    }
}
