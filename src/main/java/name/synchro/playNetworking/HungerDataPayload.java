package name.synchro.playNetworking;

import name.synchro.Synchro;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HungerDataPayload(float saturation, float exhaustion) implements ModPayload {
    public static final Id<HungerDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Synchro.MOD_ID, "hunger_data_packet"));
    public static final PacketCodec<PacketByteBuf, HungerDataPayload> CODEC = PacketCodec.of(
            HungerDataPayload::encodeBuf, HungerDataPayload::decodeBuf);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static void encodeBuf(HungerDataPayload payload, PacketByteBuf buf){
        buf.writeFloat(payload.saturation());
        buf.writeFloat(payload.exhaustion());
    }

    private static HungerDataPayload decodeBuf(PacketByteBuf buf){
        float saturation = buf.readFloat();
        float exhaustion = buf.readFloat();
        return new HungerDataPayload(saturation, exhaustion);
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends ModPayload> getCodec() {
        return CODEC;
    }
}
