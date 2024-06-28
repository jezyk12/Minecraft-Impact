package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.fluids.FluidHelper;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin {
    @Shadow @Final protected RenderedChunk[][] chunks;

    @Inject(method = "getFluidState", at = @At(value = "RETURN"), cancellable = true)
    private void fixGetFluidState(BlockPos pos, CallbackInfoReturnable<FluidState> cir,
                                  @Local(ordinal = 0) int i, @Local(ordinal = 1) int j){
        cir.setReturnValue(((FluidHelper.ForRenderedChunkSection) this.chunks[i][j]).synchro$getFluidState(pos));
        cir.cancel();
    }
}
