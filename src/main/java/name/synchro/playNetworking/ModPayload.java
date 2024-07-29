package name.synchro.playNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public interface ModPayload extends CustomPayload {
    PacketCodec<PacketByteBuf, ? extends ModPayload> getCodec();
}
