package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import name.synchro.synchroBlocks.AbstractFarmlandBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @Inject(method = "canPlantOnTop",
            at = @At("HEAD"),
            cancellable = true)
    private void canPlantOnTopMixin(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(floor.isOf(Blocks.FARMLAND)||floor.getBlock() instanceof AbstractFarmlandBlock);
    }
    @Inject(method = "getAvailableMoisture",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private static void getAvailableMoistureMixin
            (Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir,
             @Local(ordinal = 1) BlockPos blockPos,
             @Local(ordinal = 1) LocalFloatRef g,
             @Local(ordinal = 0)int i,
             @Local(ordinal = 1)int j){
        BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
        if (blockState.getBlock() instanceof AbstractFarmlandBlock) {
            g.set(1.0f);
            if (blockState.get(AbstractFarmlandBlock.MOISTURE) > 0) {
                g.set(3.0f);
            }
        }
    }
}
