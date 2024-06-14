package name.synchro.fluids;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SingleFluidUpdateS2CPacket implements FabricPacket {
    final BlockPos pos;
    final FluidState fluidState;

    public static final PacketType<SingleFluidUpdateS2CPacket> TYPE =
            PacketType.create(new Identifier("single_fluid_update"), SingleFluidUpdateS2CPacket::new);

    public SingleFluidUpdateS2CPacket(BlockPos pos, FluidState fluidState) {
        this.pos = pos;
        this.fluidState = fluidState;
    }

    SingleFluidUpdateS2CPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.fluidState = buf.readRegistryValue(Fluid.STATE_IDS);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeRegistryValue(Fluid.STATE_IDS, this.fluidState);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
