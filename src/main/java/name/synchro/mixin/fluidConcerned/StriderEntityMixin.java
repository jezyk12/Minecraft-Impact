package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StriderEntity.class)
public class StriderEntityMixin {
    @WrapOperation(method = "getPathfindingFavor", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Local(argsOnly = true)WorldView worldView,
                                        @Local(argsOnly = true)BlockPos pos){
        return worldView.getFluidState(pos);
    }
}
