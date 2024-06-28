package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, FluidHelper.ForWorld {
    @Shadow public abstract boolean isClient();
    @Shadow public abstract FluidState getFluidState(BlockPos pos);

    @Shadow @Final public boolean isClient;

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow public abstract boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth);

    @Override
    public boolean setFluidState(BlockPos pos, FluidState state, int flags, int maxDepth) {
        return FluidUtil.worldSetFluidState(((World)(Object)this), pos, state, flags, maxDepth);
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("HEAD"), cancellable = true)
    private void fluidBlockRedirectToSetFluidState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir){
        if (state.getBlock() instanceof FluidBlock) {
            FluidState fluidState = state.getFluidState();
            if (!this.getBlockState(pos).isAir()){
                this.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
            this.setFluidState(pos, fluidState);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateNeighbors(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V", shift = At.Shift.BEFORE))
    private void updateFluidOnSelfPos(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir){
        Fluid fluid = this.getFluidState(pos).getFluid();
        this.scheduleFluidTick(pos, fluid, fluid.getTickRate(this));
    }

}
