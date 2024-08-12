package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.spawner.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {
    @WrapOperation(method = "spawnPillager", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Local(argsOnly = true) ServerWorld world,
                                        @Local(argsOnly = true) BlockPos pos){

        return world.getFluidState(pos);
    }
}
