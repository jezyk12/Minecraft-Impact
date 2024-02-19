package name.synchro.synchroBlocks;

import name.synchro.registrations.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public class WaterVaporGas extends AbstractGas{
    public WaterVaporGas() {
        super(2,8,3);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return RegisterBlocks.WATER_VAPOR_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }
}
