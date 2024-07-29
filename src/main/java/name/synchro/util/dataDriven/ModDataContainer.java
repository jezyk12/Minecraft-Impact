package name.synchro.util.dataDriven;

import com.google.gson.JsonElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Identifier;

public interface ModDataContainer<T> {
    T data();
    Identifier getId();
    void deserialize(JsonElement json);
    void readBuf(RegistryByteBuf registryByteBuf);
    void writeBuf(RegistryByteBuf registryByteBuf);
}
