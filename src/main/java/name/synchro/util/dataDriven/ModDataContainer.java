package name.synchro.util.dataDriven;

import com.google.gson.JsonElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface ModDataContainer<T> {
    Identifier getId();
    T deserialize(JsonElement json);
    T readBuf(PacketByteBuf buf);
    PacketByteBuf writeBuf(PacketByteBuf buf);
}
