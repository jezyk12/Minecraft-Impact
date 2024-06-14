package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderedChunk.class)
public class RenderedChunkSectionMixin implements FluidHelper.ForRenderedChunkSection {
    @Shadow @Final private WorldChunk chunk;
    @Unique
    private List<PalettedContainer<FluidState>> fluidStateContainers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initFluidStorage(WorldChunk chunk, CallbackInfo ci){
        if (chunk instanceof EmptyChunk) {
            this.fluidStateContainers = null;
        } else {
            ChunkSection[] chunkSections = chunk.getSectionArray();
            this.fluidStateContainers = new ArrayList<>(chunkSections.length);
            for (ChunkSection chunkSection : chunkSections) {
                this.fluidStateContainers.add(chunkSection.isEmpty() ? null :
                        ((FluidHelper.ForChunkSection) chunkSection).getFluidStateContainer().copy());
            }
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return FluidUtil.getFluidStateInRenderedChunk(this.fluidStateContainers, pos, this.chunk);
    }
}
