package name.synchro.fluids.gases;

import name.synchro.registrations.BlocksRegistered;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class WaterVaporGas extends Gas {
    public WaterVaporGas() {
        super(0x00007f, 3,6,3, 80);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return BlocksRegistered.WATER_VAPOR_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
