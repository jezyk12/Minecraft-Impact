package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 0))
    private FluidState getFluidStateDown(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.down());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 1))
    private FluidState getFluidStateUp(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.up());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 2))
    private FluidState getFluidStateNorth(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.north());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 3))
    private FluidState getFluidStateSouth(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.south());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 4))
    private FluidState getFluidStateWest(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.west());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 5))
    private FluidState getFluidStateEast(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.east());
    }

    @WrapOperation(method = "getFluidHeight(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/util/math/BlockPos;)F", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 0))
    private FluidState getFluidHeightByPos(BlockState instance, Operation<FluidState> original,
                                         @Local(argsOnly = true)BlockRenderView world,
                                         @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos);
    }

    @WrapOperation(method = "getFluidHeight(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)F", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 0))
    private FluidState getFluidHeightWithTypeByPos(BlockState instance, Operation<FluidState> original,
                                           @Local(argsOnly = true)BlockRenderView world,
                                           @Local(argsOnly = true) BlockPos pos){
        return world.getFluidState(pos.up());
    }

}
