package name.synchro.util.dataDriven;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CowWorkingFeedsData implements ModDataContainer<Set<CowWorkingFeedsData.Entry>> {
    public static final Identifier ID = Identifier.of(Synchro.MOD_ID, "cow_working_feed");
    public static final Codec<Set<Entry>> CODEC = Codec.list(Entry.CODEC.codec()).comapFlatMap(entries ->
            DataResult.success(Set.copyOf(entries)), List::copyOf);
    private Set<Entry> data = new HashSet<>();

    @Override
    public Set<Entry> data() {
        return data;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void deserialize(JsonElement json) {
        try {
            JsonArray feeds = json.getAsJsonObject().getAsJsonArray("feeds");
            DataResult<Set<Entry>> result = CODEC.parse(JsonOps.INSTANCE, feeds);
            this.data = result.getOrThrow();
        } catch (Exception e) {
            Synchro.LOGGER.error("Unable to parse {}: ", getId(), e);
        }
    }

    @Override
    public void readBuf(RegistryByteBuf buf) {
        ImmutableSet.Builder<Entry> builder = ImmutableSet.builder();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Ingredient items = Ingredient.PACKET_CODEC.decode(buf);
            int time = buf.readInt();
            builder.add(new Entry(items, time));
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

    public record Entry(Ingredient items, int time){
        public static final MapCodec<Entry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("items").forGetter(Entry::items),
                Codec.INT.fieldOf("time").forGetter(Entry::time)
        ).apply(instance, Entry::new));
    }
}
