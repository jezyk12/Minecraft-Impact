package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {
    @WrapOperation(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 0))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Local(argsOnly = true) World world,
                                        @Local(ordinal = 0) BlockPos.Mutable pos){

        return world.getFluidState(pos);
    }

    @WrapOperation(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 1))
    private FluidState fixGetFluidState1(BlockState instance, Operation<FluidState> original,
                                        @Local(argsOnly = true) World world,
                                        @Local(ordinal = 1) BlockPos.Mutable pos){

        return world.getFluidState(pos);
    }

    @WrapOperation(method = "getFluidStateIfVisible", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidStateIfVisible(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true) World world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos);
    }
}
