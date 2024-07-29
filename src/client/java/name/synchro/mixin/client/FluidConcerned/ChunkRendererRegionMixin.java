package name.synchro.mixin.client.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import name.synchro.fluids.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin {

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/RenderedChunk;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState getRenderedChunk(RenderedChunk instance, BlockPos pos, Operation<BlockState> original,
                                        @Share("renderedChunkRef") LocalRef<RenderedChunk> renderedChunkLocalRef,
                                        @Share("blockPosRef")LocalRef<BlockPos> blockPosRef){
        renderedChunkLocalRef.set(instance);
        blockPosRef.set(pos);
        return original.call(instance, pos);
    }

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Share("renderedChunkLocalRef") LocalRef<RenderedChunk> renderedChunkLocalRef,
                                        @Share("blockPosRef") LocalRef<BlockPos> blockPosLocalRef){
        return ((FluidHelper.ForRenderedChunk) renderedChunkLocalRef.get()).synchro$getFluidState(blockPosLocalRef.get());
    }
}
