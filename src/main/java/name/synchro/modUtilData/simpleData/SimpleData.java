package name.synchro.modUtilData.simpleData;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import name.synchro.Synchro;
import name.synchro.registrations.ModRegistries;

public interface SimpleData<T extends SimpleData<T>> {
    Codec<SimpleData<?>> CODEC = ModRegistries.SIMPLE_DATA_TYPE.getCodec().dispatch("as", SimpleData::getType, SimpleData.Type::codec);
    Type<T> getType();
    T get();
    record Type<T extends SimpleData<T>>(MapCodec<T> codec) {}
    static void registerAll(){
        Synchro.LOGGER.debug("Registered simple data for " + Synchro.MOD_ID);
    }

}
