package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    @WrapOperation(method = "onRandomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z"))
    private boolean lightFireTest(BlockState instance, Operation<Boolean> original){
        return original.call(instance) || instance.getBlock() instanceof FluidBlock && !instance.isOf(Blocks.WATER);
    }

    @WrapOperation(method = "onRandomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAir(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean lightFireTest1(World instance, BlockPos pos, Operation<Boolean> original){
        return original.call(instance, pos) || instance.getBlockState(pos).getBlock() instanceof FluidBlock && !instance.getFluidState(pos).isIn(FluidTags.WATER);
    }

//    @Inject(method = "playExtinguishEvent", at = @At("HEAD"))
//    private void debug(WorldAccess world, BlockPos pos, CallbackInfo ci){
//        System.out.println("sss!");
//    }
}
