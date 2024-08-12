package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    @WrapOperation(method = "tryGrow(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PointedDripstoneBlock;canGrow(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private static boolean fixGrowingTest(BlockState dripstoneBlockState, BlockState waterState, Operation<Boolean> original,
                                          @Local(argsOnly = true)ServerWorld world, @Local(argsOnly = true)BlockPos pos){
        return dripstoneBlockState.isOf(Blocks.DRIPSTONE_BLOCK) && world.getFluidState(pos.up(2)).isEqualAndStill(Fluids.WATER);
    }
}
