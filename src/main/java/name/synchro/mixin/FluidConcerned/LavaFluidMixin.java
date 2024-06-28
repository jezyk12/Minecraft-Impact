package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
    @WrapOperation(method = "flow", constant = @Constant(classValue = FluidBlock.class))
    private boolean wrapFluidBlockTest(Object object, Operation<Boolean> original){
        return true;
    }

    @WrapOperation(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean onLavaFormsStone(WorldAccess world, BlockPos pos, BlockState blockState, int flags, Operation<Boolean> original){
        if (FluidUtil.onLavaFormsStone(world, pos)) return original.call(world, pos, blockState, flags);
        return true;
    }
}
