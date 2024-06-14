package name.synchro.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Deprecated
public class BlockUpdateWithFluidS2CPacket extends BlockUpdateS2CPacket {
    FluidState fluidState;

    public BlockUpdateWithFluidS2CPacket(BlockPos pos, BlockState state, FluidState fluidState) {
        super(pos, state);
        this.fluidState = fluidState;
    }

    public BlockUpdateWithFluidS2CPacket(BlockView world, BlockPos pos) {
        super(world, pos);
        this.fluidState = world.getFluidState(pos);
    }

    public BlockUpdateWithFluidS2CPacket(PacketByteBuf buf) {
        super(buf);
        this.fluidState = buf.readRegistryValue(Fluid.STATE_IDS);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeRegistryValue(Fluid.STATE_IDS, this.fluidState);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ((FluidHelper.ForClientPacketListener)listener).onBlockUpdateWithFluid(this);
    }

    public FluidState getFluidState() {
        return this.fluidState;
    }
}
