package name.synchro.modUtilData.simpleData;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import net.minecraft.util.Identifier;

import java.util.List;

@Deprecated
public record CowFeedDataCollection(List<CowFeedDataEntry> data) implements SimpleData<CowFeedDataCollection> {

    public static final Identifier ID = Identifier.of(Synchro.MOD_ID, "cow_working_feed");

    @Override
    public Type<CowFeedDataCollection> getType() {
        return null;//SimpleData.COW_FEED_DATA_COLLECTION;
    }

    @Override
    public CowFeedDataCollection get() {
        return this;
    }

}
