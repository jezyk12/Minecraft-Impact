package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {
    @Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);



    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private boolean onLavaFormsObsidianOrCobbleStone(World instance, BlockPos pos, BlockState state, Operation<Boolean> original,
                                                     @Share("extinguish")LocalBooleanRef extinguish){
        if (state.isOf(Blocks.OBSIDIAN)){
            if (FluidUtil.onLavaFormsObsidian(instance, pos)) {
                extinguish.set(true);
                return original.call(instance, pos, state);
            }
        }
        else if (state.isOf(Blocks.COBBLESTONE)){
            if (FluidUtil.onLavaFormsCobblestone(instance, pos)) {
                extinguish.set(true);
                return original.call(instance, pos, state);
            }
        }
        return true;
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private boolean onLavaFormsBasalt(World instance, BlockPos pos, BlockState state, Operation<Boolean> original,
                                      @Share("extinguish")LocalBooleanRef extinguish){
        if (FluidUtil.onLavaFormsBasalt(instance, pos)) {
            extinguish.set(true);
            return original.call(instance, pos, state);
        }
        return true;
    }

    @WrapWithCondition(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidBlock;playExtinguishSound(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
    private boolean preventIncorrectEvent(FluidBlock instance, WorldAccess world, BlockPos pos,
                                          @Share("extinguish")LocalBooleanRef extinguish){
        return extinguish.get();
    }

    //    @Inject(method = "getOutlineShape", at = @At("RETURN"), cancellable = true)
    //    private void debugGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
    //        cir.setReturnValue(VoxelShapes.fullCube());
    //        cir.cancel();
    //    }
}
