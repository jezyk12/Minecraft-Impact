package name.synchro.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.PalettedContainer;
import org.jetbrains.annotations.Nullable;

public final class FluidHelper {

    public interface ForRenderedChunk {
        FluidState synchro$getFluidState(BlockPos pos);
    }

    public interface ForWorld {
        boolean synchro$setFluidState(BlockPos pos, FluidState state, int flags, int maxDepth);
        default boolean synchro$setFluidState(BlockPos pos, FluidState state){
            return synchro$setFluidState(pos, state, Block.NOTIFY_ALL, 512);
        }

    }

    public interface ForChunk {
        FluidState synchro$setFluidState(BlockPos pos, FluidState state);
    }

    public interface ForChunkSection {
        FluidState synchro$setFluidStateLocally(int x, int y, int z, FluidState state);

        PalettedContainer<FluidState> synchro$getFluidStateContainer();

        void synchro$setFluidStateContainer(PalettedContainer<FluidState> container);

        void synchro$countFluidStates();
    }

    public interface ForBucketItem {
        Fluid synchro$getFluid();

        void synchro$callPlayEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos);
    }

    @Deprecated
    public interface ForClientPacketListener {

    }

    @Deprecated
    public interface ForClientWorld {
        void synchro$handleBlockUpdateWithFluid(BlockPos pos, BlockState state, FluidState fluidState, int flags);
    }
}
