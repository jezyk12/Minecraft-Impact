package name.synchro.fluids.gases;

import name.synchro.registrations.BlocksRegistered;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class StronglyOxidizingGas extends Gas{
    public StronglyOxidizingGas() {
        super(0xFF9696, 6, 5, 5, 100);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return BlocksRegistered.STRONGLY_OXIDIZING_GAS_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
