package name.synchro.playNetworking;

import name.synchro.Synchro;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FireTicksDataPayload(int fireTicks) implements ModPayload {
    public static final Id<FireTicksDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Synchro.MOD_ID, "fire_ticks_data_packet"));
    public static final PacketCodec<PacketByteBuf, FireTicksDataPayload> CODEC = PacketCodec.of(
            FireTicksDataPayload::encodeBuf, FireTicksDataPayload::decodeBuf);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static void encodeBuf(FireTicksDataPayload payload, PacketByteBuf buf){
        buf.writeInt(payload.fireTicks());
    }

    private static FireTicksDataPayload decodeBuf(PacketByteBuf buf){
        return new FireTicksDataPayload(buf.readInt());
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends ModPayload> getCodec() {
        return CODEC;
    }
}
