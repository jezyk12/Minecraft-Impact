package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {
    @Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

    @Inject(method = "getOutlineShape", at = @At("RETURN"), cancellable = true)
    private void debugGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        cir.setReturnValue(VoxelShapes.fullCube());
        cir.cancel();
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private boolean onLavaFormsObsidianOrCobbleStone(World instance, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (state.isOf(Blocks.OBSIDIAN)){
            if (FluidUtil.onLavaFormsObsidian(instance, pos)) {
                this.playExtinguishSound(instance, pos);
                return original.call(instance, pos, state);
            }
        }
        else if (state.isOf(Blocks.COBBLESTONE)){
            if (FluidUtil.onLavaFormsCobblestone(instance, pos)) {
                this.playExtinguishSound(instance, pos);
                return original.call(instance, pos, state);
            }
        }
        return true;
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private boolean onLavaFormsBasalt(World instance, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (FluidUtil.onLavaFormsBasalt(instance, pos)) {
            this.playExtinguishSound(instance, pos);
            return original.call(instance, pos, state);
        }
        return true;
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidBlock;playExtinguishSound(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
    private void preventIncorrectEvent(FluidBlock instance, WorldAccess world, BlockPos pos, Operation<Void> original){
        // Do nothing.
    }
}
