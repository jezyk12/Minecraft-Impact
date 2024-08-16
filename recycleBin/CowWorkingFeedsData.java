package name.synchro.modUtilData.dataEntries;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import name.synchro.Synchro;
import name.synchro.modUtilData.ModDataContainer;
import name.synchro.modUtilData.simpleData.CowFeedDataCollection;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
public final class CowWorkingFeedsData implements ModDataContainer<Set<CowFeedDataEntry>> {
    public static final Codec<Set<CowFeedDataEntry>> CODEC = Codec.list(CowFeedDataEntry.CODEC.codec()).comapFlatMap(entries ->
            DataResult.success(Set.copyOf(entries)), List::copyOf);
    private Set<CowFeedDataEntry> data = new HashSet<>();

    @Override
    public Set<CowFeedDataEntry> data() {
        return data;
    }

    @Override
    public Identifier getId() {
        return CowFeedDataCollection.ID;
    }

    @Override
    public void deserialize(JsonElement json, Identifier identifier) {
        try {
            DataResult<Set<CowFeedDataEntry>> result = CODEC.parse(JsonOps.INSTANCE, json);
            this.data = result.getOrThrow();
        } catch (Exception e) {
            Synchro.LOGGER.error("Unable to parse {}: ", identifier, e);
        }
    }

    @Override
    public void readBuf(RegistryByteBuf buf) {
        ImmutableSet.Builder<CowFeedDataEntry> builder = ImmutableSet.builder();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Ingredient items = Ingredient.PACKET_CODEC.decode(buf);
            int time = buf.readInt();
            builder.add(new CowFeedDataEntry(items, time));
        }
        this.data = builder.build();
    }

    @Override
    public void writeBuf(RegistryByteBuf buf) {
        buf.writeInt(data.size());
        data.forEach(entry -> {
            Ingredient.PACKET_CODEC.encode(buf, entry.items());
            buf.writeInt(entry.time());
        });
    }

}
