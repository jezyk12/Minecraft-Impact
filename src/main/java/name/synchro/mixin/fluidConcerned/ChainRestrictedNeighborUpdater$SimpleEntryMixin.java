package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$SimpleEntry")
public class ChainRestrictedNeighborUpdater$SimpleEntryMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState addPotentialFluidUpdate(World world, BlockPos pos, Operation<BlockState> original) {
        BlockState blockState = original.call(world, pos);
        FluidState fluidState = world.getFluidState(pos);
        if (blockState.isAir()){
            return fluidState.getBlockState();
        }
        else {
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return blockState;
        }
    }
}
