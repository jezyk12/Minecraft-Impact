package name.synchro.api;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import name.synchro.modUtilData.dataEntries.CowWorkingFeedsData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class CowWorkingFeedDataProvider extends FabricCodecDataProvider<Set<CowFeedDataEntry>> {
    protected static final String NAME = "Cow Feeds Data";
    protected static final Identifier COW_WORKING_FEED_FILE = Identifier.of(Synchro.MOD_ID, "cow_working_feed");
    private final Set<CowFeedDataEntry> entries = new HashSet<>();

    public CowWorkingFeedDataProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture, DataOutput.OutputType.DATA_PACK, ModDataLoader.DICTIONARY_ROOT, CowWorkingFeedsData.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, Set<CowFeedDataEntry>> provider, RegistryWrapper.WrapperLookup lookup) {
        generate(lookup);
        provider.accept(COW_WORKING_FEED_FILE, this.entries);
        this.entries.clear();
    }

    public abstract void generate(RegistryWrapper.WrapperLookup wrapperLookup);

    public void addEntry(Ingredient ingredient, int time){
        this.entries.add(new CowFeedDataEntry(ingredient, time));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
