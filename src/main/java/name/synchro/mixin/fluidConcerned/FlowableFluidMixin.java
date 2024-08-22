package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {
    @Shadow protected abstract void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state);

    @ModifyReturnValue(method = "method_15734", at = @At("RETURN"))
    private static Pair<BlockState, FluidState>
    fixGetFluidStateInGetSpread(Pair<BlockState, FluidState> original,
                                @Local(argsOnly = true)World world, @Local(argsOnly = true)BlockPos pos){
        return original.mapSecond(fluidState -> world.getFluidState(pos));
    }

//    @ModifyArg(method = "getSpread", at = @At(value = "INVOKE",
//            target = "Lit/unimi/dsi/fastutil/shorts/Short2ObjectMap;computeIfAbsent(SLit/unimi/dsi/fastutil/shorts/Short2ObjectFunction;)Ljava/lang/Object;",
//            ordinal = 0), index = 1)
//    private Short2ObjectFunction<? extends Pair<BlockState, FluidState>> fixGetFluidStateInGetSpread(
//            Short2ObjectFunction<? extends Pair<BlockState, FluidState>> mappingFunction,
//            @Local(argsOnly = true)World world, @Local(ordinal = 1) BlockPos blockPos){
//        return (sx) -> {
//            BlockState blockState = world.getBlockState(blockPos);
//            FluidState fluidState = world.getFluidState(blockPos);
//            return Pair.of(blockState, fluidState);
//        };
//    }

    @WrapOperation(method = "canFlowDownTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidStateInCanFlowDownTo(
            BlockState instance, Operation<FluidState> original,
            @Local(argsOnly = true)BlockView world,
            @Local(argsOnly = true, ordinal = 1)BlockPos fromPos){
        return world.getFluidState(fromPos);
    }

    @ModifyReturnValue(method = "method_15755", at = @At("RETURN"))
    private static Pair<BlockState, FluidState>
    fixGetFluidStateInGetMinFlowDowDistance(Pair<BlockState, FluidState> original,
                                            @Local(argsOnly = true) WorldView world, @Local(argsOnly = true)BlockPos pos){
        return original.mapSecond(fluidState -> world.getFluidState(pos));
    }

    @WrapOperation(method = "getUpdatedState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;", ordinal = 0))
    private FluidState fixGetFluidStateInGetUpdatedState0(
            BlockState instance, Operation<FluidState> original,
            @Local(argsOnly = true)World world, @Local(ordinal = 1)BlockPos blockPos){
        return world.getFluidState(blockPos);
    }

    @WrapOperation(method = "getUpdatedState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;", ordinal = 1))
    private FluidState fixGetFluidStateInGetUpdatedState1(
            BlockState instance, Operation<FluidState> original,
            @Local(argsOnly = true)World world, @Local(argsOnly = true)BlockPos pos){
        return world.getFluidState(pos.down());
    }

    @WrapOperation(method = "getUpdatedState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;", ordinal = 2))
    private FluidState fixGetFluidStateInGetUpdatedState2(
            BlockState instance, Operation<FluidState> original,
            @Local(argsOnly = true)World world, @Local(argsOnly = true)BlockPos pos){
        return world.getFluidState(pos.up());
    }

    @Inject(method = "onScheduledTick", at = @At("HEAD"))
    private void testBlockCoexist(World world, BlockPos pos, FluidState state, CallbackInfo ci){
        FluidUtil.onBlockCoexistWithFluid(world, pos, state);
    }

    @WrapOperation(method = "onScheduledTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
    private boolean fixDrainFluid(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original){
        return ((FluidHelper.ForWorld) world).synchro$setFluidState(pos, Fluids.EMPTY.getDefaultState(), Block.NOTIFY_ALL, 512);
    }

    @WrapOperation(method = "onScheduledTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1))
    private boolean fixDescendFluid(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original, @Local(ordinal = 1)FluidState fluidState){
        return ((FluidHelper.ForWorld) world).synchro$setFluidState(pos, fluidState, Block.NOTIFY_LISTENERS, 512);
    }

    @Inject(method = "canFill", at = @At("HEAD"), cancellable = true)
    private void modifyCanFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir){
        boolean covered = FluidUtil.canBlockReplaceFluid(world, pos, state, 1.0f);
        cir.setReturnValue(!covered);
        cir.cancel();
    }

    @Inject(method = "flow", at = @At("HEAD"), cancellable = true)
    private void modifyFlow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci){
        if (!state.isAir() && FluidUtil.canBlockWashAway(state, fluidState.getFluid())) {
            beforeBreakingBlock(world, pos, state);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        ((FluidHelper.ForWorld)world).synchro$setFluidState(pos, fluidState, Block.NOTIFY_ALL, 512);
        ci.cancel();
    }

    @Inject(method = "canFlow", at = @At("RETURN"), cancellable = true)
    private void judgeCanFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir){
        boolean original = cir.getReturnValue();
        FluidUtil.judgeCanFlow(world, fluidPos, fluidBlockState, flowDirection, flowTo, flowToBlockState, fluidState, fluid, original).ifPresent(result -> {
            cir.setReturnValue(result);
            cir.cancel();
        });
    }
}
