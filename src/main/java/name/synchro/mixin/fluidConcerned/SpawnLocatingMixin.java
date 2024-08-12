package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Deprecated
@Mixin(SpawnLocating.class)
public class SpawnLocatingMixin {
    @WrapOperation(method = "findOverworldSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private static FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                               @Local(argsOnly = true) ServerWorld world,
                                               @Local BlockPos.Mutable pos){

        return world.getFluidState(pos);
    }
}
