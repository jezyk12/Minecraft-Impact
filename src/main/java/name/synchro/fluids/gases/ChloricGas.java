package name.synchro.fluids.gases;

import name.synchro.registrations.BlocksRegistered;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class ChloricGas extends Gas{
    public ChloricGas() {
        super(0xf0ff00, 4, 2, 2, 30);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return BlocksRegistered.CHLORIC_GAS_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
