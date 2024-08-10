package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import name.synchro.modUtilData.ModDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, FluidHelper.ForWorld, ModDataManager.Provider {
    @Shadow public abstract boolean isClient();
    @Shadow public abstract FluidState getFluidState(BlockPos pos);

    @Shadow @Final public boolean isClient;

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow public abstract boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth);

    @Shadow public abstract WorldChunk getChunk(int i, int j);

    @Shadow public abstract boolean setBlockState(BlockPos pos, BlockState state, int flags);

    @Override
    public boolean synchro$setFluidState(BlockPos pos, FluidState state, int flags, int maxDepth) {
        return FluidUtil.worldSetFluidState(((World) (Object) this), pos, state, flags, maxDepth);
    }

    @Override
    public ModDataManager synchro$getModDataManager() {
        return ModDataManager.DUMMY;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;", shift = At.Shift.AFTER))
    private void onSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir){
        if (state.isAir()) ((FluidHelper.ForChunk)this.getChunk(pos)).synchro$setFluidState(pos, Fluids.EMPTY.getDefaultState());
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateNeighbors(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V", shift = At.Shift.BEFORE))
    private void updateFluidOnSelfPos(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir){
        Fluid fluid = this.getFluidState(pos).getFluid();
        this.scheduleFluidTick(pos, fluid, fluid.getTickRate(this));
    }

}
