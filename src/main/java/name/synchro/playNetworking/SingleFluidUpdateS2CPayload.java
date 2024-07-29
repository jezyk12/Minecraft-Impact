package name.synchro.playNetworking;

import name.synchro.Synchro;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SingleFluidUpdateS2CPayload(BlockPos pos, FluidState fluidState) implements ModPayload {
    public static final Identifier SINGLE_FLUID_UPDATE_ID = Identifier.of(Synchro.MOD_ID, "single_fluid_update");
    public static Id<SingleFluidUpdateS2CPayload> ID = new Id<>(SINGLE_FLUID_UPDATE_ID);
    public static PacketCodec<PacketByteBuf, SingleFluidUpdateS2CPayload> CODEC = PacketCodec.of(SingleFluidUpdateS2CPayload::encode, SingleFluidUpdateS2CPayload::decode);

    private static SingleFluidUpdateS2CPayload decode(PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        FluidState fluidState = PacketCodecs.entryOf(Fluid.STATE_IDS).decode(buf);
        return new SingleFluidUpdateS2CPayload(pos, fluidState);
    }

    private static void encode(SingleFluidUpdateS2CPayload payload, PacketByteBuf buf) {
        buf.writeBlockPos(payload.pos());
        PacketCodecs.entryOf(Fluid.STATE_IDS).encode(buf, payload.fluidState());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends ModPayload> getCodec() {
        return CODEC;
    }
}
