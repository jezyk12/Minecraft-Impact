package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.Synchro;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    /**
     * {@link FluidBlock#getFluidState} should not be called due to the new format of fluid storage in chunk.
     * Fluid states are no longer stored in block states. If it's invoked by another mod, please report this to the developers.
     * It's recommended to use {@link World#getFluidState} with BlockPos parameter instead or the game may not run as expected.
     */
    @Inject(method = "getFluidState", at = @At("HEAD"))
    private void warnsWhenGetFluidState(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        Synchro.LOGGER.warn("FluidBlock#getFluidState has been called unexpectedly.");
        if (!Synchro.fluidStorageWarned){
            Synchro.LOGGER.debug("FluidBlock#getFluidState should not be called due to the new format of fluid storage in chunk. Fluid states are no longer stored in block states. If it's invoked by another mod, please report this to the developers. It's recommended to use World#getFluidState with BlockPos parameter instead or the game may not run as expected.");
            Synchro.fluidStorageWarned = true;
        }
    }

    @Inject(method = "getOutlineShape", at = @At("RETURN"), cancellable = true)
    private void debugGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        cir.setReturnValue(VoxelShapes.fullCube());
        cir.cancel();
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private boolean onLavaFormsObsidianOrCobbleStone(World instance, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (state.isOf(Blocks.OBSIDIAN)){
            if (FluidUtil.onLavaFormsObsidian(instance, pos)) return original.call(instance, pos, state);
        }
        else if (state.isOf(Blocks.COBBLESTONE)){
            if (FluidUtil.onLavaFormsCobblestone(instance, pos)) return original.call(instance, pos, state);
        }
        return true;
    }

    @WrapOperation(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private boolean onLavaFormsBasalt(World instance, BlockPos pos, BlockState state, Operation<Boolean> original){
        if (FluidUtil.onLavaFormsBasalt(instance, pos)) return original.call(instance, pos, state);
        return true;
    }
}
