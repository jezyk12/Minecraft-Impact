package name.synchro.modUtilData.dataEntries;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import name.synchro.registrations.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Set;

public record CowFeedDataEntry(Ingredient items, int time) {
    public static final MapCodec<CowFeedDataEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("items").forGetter(CowFeedDataEntry::items),
            Codec.INT.fieldOf("time").forGetter(CowFeedDataEntry::time)
    ).apply(instance, CowFeedDataEntry::new));

    private static ImmutableSet<CowFeedDataEntry> CACHE;

    public static boolean isFeed(World world, ItemStack stack){
        return getData(world).stream().anyMatch(entry -> entry.items().test(stack));
    }

    public static int getFeedTime(World world, ItemStack stack){
        return getData(world).stream().filter(entry -> entry.items().test(stack))
                .max(Comparator.comparingInt(CowFeedDataEntry::time))
                .map(CowFeedDataEntry::time).orElse(0);
    }

    public static Set<CowFeedDataEntry> getData(World world){
        if (CowFeedDataEntry.CACHE == null){
            if (world != null) {
                try {
                    ImmutableSet.Builder<CowFeedDataEntry> builder = ImmutableSet.builder();
                    world.getRegistryManager().getWrapperOrThrow(ModRegistries.Keys.COW_FEEDS_DATA_ENTRIES)
                            .streamEntries().map(RegistryEntry.Reference::value).forEach(builder::add);
                    CowFeedDataEntry.CACHE = builder.build();
                } catch (Exception e) {
                    Synchro.LOGGER.error("Cannot load cowFeedsData!", e);
                }
            }
        }
        return CowFeedDataEntry.CACHE;
    }

    public static void onReload(){
        CowFeedDataEntry.CACHE = null;
    }
}
