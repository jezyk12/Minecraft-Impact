package name.synchro.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.PalettedContainer;

public final class FluidHelper {

    public interface ForRenderedChunkSection {
        FluidState getFluidState(BlockPos pos);
    }

    public interface ForWorld {
        boolean setFluidState(BlockPos pos, FluidState state, int flags, int maxDepth);
        default boolean setFluidState(BlockPos pos, FluidState state){
            return setFluidState(pos, state, Block.NOTIFY_ALL, 512);
        }

    }

    public interface ForChunk {
        FluidState setFluidState(BlockPos pos, FluidState state);
    }

    public interface ForChunkSection {
        FluidState setFluidStateLocally(int x, int y, int z, FluidState state);

        PalettedContainer<FluidState> getFluidStateContainer();

        void setFluidStateContainer(PalettedContainer<FluidState> container);

        void countFluidStates();
    }

    @Deprecated
    public interface ForClientPacketListener {
        void onChunkUpdateWithFluid(ChunkDeltaUpdateWithFluidS2CPacket packet);

        void onBlockUpdateWithFluid(BlockUpdateWithFluidS2CPacket packet);
    }

    @Deprecated
    public interface ForClientWorld {
        void handleBlockUpdateWithFluid(BlockPos pos, BlockState state, FluidState fluidState, int flags);
    }
}
