package name.synchro.fluids.gases;

import name.synchro.registrations.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class StronglyReducingGas extends Gas{
    public StronglyReducingGas() {
        super(0xB700FF, 6,5,5, 100);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ModBlocks.STRONGLY_REDUCING_GAS_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
