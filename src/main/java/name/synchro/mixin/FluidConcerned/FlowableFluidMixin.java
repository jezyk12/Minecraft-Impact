package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import name.synchro.fluids.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {
    @ModifyArg(method = "getSpread", at = @At(value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/shorts/Short2ObjectMap;computeIfAbsent(SLit/unimi/dsi/fastutil/shorts/Short2ObjectFunction;)Ljava/lang/Object;",
            ordinal = 0), index = 1)
    private Short2ObjectFunction<? extends Pair<BlockState, FluidState>> fixGetFluidStateInGetSpread(
            Short2ObjectFunction<? extends Pair<BlockState, FluidState>> mappingFunction,
            @Local(argsOnly = true)World world, @Local(ordinal = 1) BlockPos blockPos){
        return (sx) -> {
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = world.getFluidState(blockPos);
            return Pair.of(blockState, fluidState);
        };
    }

    @WrapOperation(method = "canFlowDownTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidStateInCanFlowDownTo(
            BlockState instance, Operation<FluidState> original,
            @Local(argsOnly = true)BlockView world,
            @Local(argsOnly = true, ordinal = 1)BlockPos fromPos){
        return world.getFluidState(fromPos);
    }

    @ModifyArg(method = "getFlowSpeedBetween", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/shorts/Short2ObjectMap;computeIfAbsent(SLit/unimi/dsi/fastutil/shorts/Short2ObjectFunction;)Ljava/lang/Object;", ordinal = 0), index = 1)
    private Short2ObjectFunction<? extends Pair<BlockState, FluidState>> fixGetFluidStateInGetFlowSpeedBetween(
            Short2ObjectFunction<? extends Pair<BlockState, FluidState>> mappingFunction,
            @Local(argsOnly = true) WorldView world, @Local(ordinal = 2) BlockPos blockPos){
        return (sx) -> {
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = world.getFluidState(blockPos);
            return Pair.of(blockState, fluidState);
        };
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

    @WrapOperation(method = "onScheduledTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
    private boolean fixDrainFluid(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original){
        return ((FluidHelper.ForWorld) world).setFluidState(pos, Fluids.EMPTY.getDefaultState(), Block.NOTIFY_ALL, 512);
    }

    @WrapOperation(method = "onScheduledTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1))
    private boolean fixDescendFluid(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original, @Local(ordinal = 1)FluidState fluidState){
        return ((FluidHelper.ForWorld) world).setFluidState(pos, fluidState, Block.NOTIFY_LISTENERS, 512);
    }
}
