package name.synchro.playNetworking;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ModDataPayload(PacketByteBuf buf) implements ModPayload {
    public static final Id<ModDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Synchro.MOD_ID, "mod_data_pack"));
    public static final PacketCodec<PacketByteBuf, ModDataPayload> CODEC = PacketCodec.of(ModDataPayload::encode, ModDataPayload::decode);

    private static void encode(ModDataPayload payload, PacketByteBuf buf){
        buf.writeInt(payload.buf().writerIndex());
        buf.writeBytes(payload.buf());
    }

    private static ModDataPayload decode(PacketByteBuf buf){
        int length = buf.readInt();
        PacketByteBuf buf1 = PacketByteBufs.readBytes(buf, length);
        return new ModDataPayload(buf1);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public PacketCodec<PacketByteBuf, ModDataPayload> getCodec() {
        return CODEC;
    }
}
