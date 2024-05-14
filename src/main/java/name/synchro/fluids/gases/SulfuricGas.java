package name.synchro.fluids.gases;

import name.synchro.registrations.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class SulfuricGas extends Gas{
    public SulfuricGas() {
        super(0xdc4d00, 5, 3, 4, 10);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return RegisterBlocks.SULFURIC_GAS_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
