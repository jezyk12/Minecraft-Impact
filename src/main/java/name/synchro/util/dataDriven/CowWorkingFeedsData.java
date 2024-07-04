package name.synchro.util.dataDriven;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import name.synchro.Synchro;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public final class CowWorkingFeedsData implements ModDataContainer<Set<CowWorkingFeedsData.Entry>> {
    public static final Identifier ID = new Identifier(Synchro.MOD_ID, "cow_working_feed");
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
            feeds.forEach(jsonElement -> {
                JsonObject entry = jsonElement.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(entry.get("items"));
                int times = entry.get("time").getAsInt();
                this.data.add(new Entry(ingredient, times));
            });
        } catch (Exception e) {
            Synchro.LOGGER.error("Unable to parse {}: ", ID, e);
        }
    }

    @Override
    public void readBuf(PacketByteBuf buf) {
        ImmutableSet.Builder<Entry> builder = ImmutableSet.builder();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Ingredient items = Ingredient.fromPacket(buf);
            int time = buf.readInt();
            builder.add(new Entry(items, time));
        }
        this.data = builder.build();
    }

    @Override
    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        buf.writeInt(data.size());
        data.forEach(entry -> {
            entry.items().write(buf);
            buf.writeInt(entry.time());
        });
        return buf;
    }

    public record Entry(Ingredient items, int time){}
}
