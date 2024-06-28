package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin {
    @WrapOperation(method = "shouldHarden", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ConcretePowderBlock;hardensIn(Lnet/minecraft/block/BlockState;)Z"))
    private static boolean fixHardensIn(BlockState state, Operation<Boolean> original,
                                        @Local(argsOnly = true)BlockView world, @Local(argsOnly = true)BlockPos pos){
        return world.getFluidState(pos).isOf(Fluids.WATER);
    }

    @WrapOperation(method = "hardensOnAnySide",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ConcretePowderBlock;hardensIn(Lnet/minecraft/block/BlockState;)Z"))
    private static boolean fixHardensIn1(BlockState state, Operation<Boolean> original,
                                        @Local(argsOnly = true)BlockView world, @Local BlockPos.Mutable pos){
        return world.getFluidState(pos).isOf(Fluids.WATER);
    }
}
