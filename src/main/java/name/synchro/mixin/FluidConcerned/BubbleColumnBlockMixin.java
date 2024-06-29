package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Deprecated
@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
    @WrapOperation(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BubbleColumnBlock;isStillWater(Lnet/minecraft/block/BlockState;)Z"))
    private boolean fixIsStillWaterTest(BlockState state, Operation<Boolean> original,
                                        @Local(argsOnly = true)WorldAccess worldAccess,
                                        @Local(argsOnly = true, ordinal = 1)BlockPos neighborPos){
        return FluidUtil.isStillWaterForBubbles(worldAccess, neighborPos);
    }

    @WrapOperation(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BubbleColumnBlock;isStillWater(Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private static boolean fixIsStillWaterTest1(BlockState state, Operation<Boolean> original,
                                                @Local(argsOnly = true) WorldAccess worldAccess,
                                                @Local(argsOnly = true) BlockPos pos){
        return FluidUtil.isStillWaterForBubbles(worldAccess, pos);
    }

    @WrapOperation(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BubbleColumnBlock;isStillWater(Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private static boolean fixIsStillWaterTest2(BlockState state, Operation<Boolean> original,
                                                @Local(argsOnly = true) WorldAccess worldAccess,
                                                @Local BlockPos.Mutable pos){
        return FluidUtil.isStillWaterForBubbles(worldAccess, pos);
    }

}
