package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin extends PathNodeMaker {
    @WrapOperation(method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
    ordinal = 0))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Local BlockPos.Mutable pos){

        return this.cachedWorld.getFluidState(pos);
    }

    @WrapOperation(method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 1))
    private FluidState fixGetFluidState1(BlockState instance, Operation<FluidState> original,
                                        @Local BlockPos.Mutable pos){

        return this.cachedWorld.getFluidState(pos);
    }

    @WrapOperation(method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;",
            ordinal = 2))
    private FluidState fixGetFluidState2(BlockState instance, Operation<FluidState> original,
                                         @Local int i){

        return this.cachedWorld.getFluidState(new BlockPos(entity.getBlockX(), i, entity.getBlockZ()));
    }
}
