package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import name.synchro.modUtilData.simpleData.SimpleData;
import name.synchro.modUtilData.locationAction.LocationAction;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ModRegistries {
    public static final class Keys{
        public static final RegistryKey<Registry<LocationAction.Type<?>>> LOCATION_ACTION_TYPE = RegistryKey.ofRegistry(Identifier.of(Synchro.MOD_ID, "location_action_type"));
        public static final RegistryKey<Registry<FluidReaction>> FLUID_REACTION = RegistryKey.ofRegistry(Synchro.id("fluid_reaction"));
        public static final RegistryKey<Registry<CowFeedDataEntry>> COW_FEEDS_DATA_ENTRIES = RegistryKey.ofRegistry(Synchro.id("cow_working_feeds"));
        public static final RegistryKey<Registry<SimpleData.Type<?>>> SIMPLE_DATA_TYPE = RegistryKey.ofRegistry(Identifier.of(Synchro.MOD_ID, "simple_data_type"));
        public static final RegistryKey<Registry<SimpleData<?>>> SIMPLE_DATA = RegistryKey.ofRegistry(Synchro.id("misc"));
    }

    public static final Registry<LocationAction.Type<?>> LOCATION_ACTION_TYPE =
            FabricRegistryBuilder.createSimple(Keys.LOCATION_ACTION_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final Registry<SimpleData.Type<?>> SIMPLE_DATA_TYPE =
            FabricRegistryBuilder.createSimple(Keys.SIMPLE_DATA_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod registries for " + Synchro.MOD_ID);
        LocationAction.registerAll();
        SimpleData.registerAll();
        DynamicRegistries.registerSynced(Keys.FLUID_REACTION, FluidReaction.CODEC.codec(), DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        DynamicRegistries.registerSynced(Keys.COW_FEEDS_DATA_ENTRIES, CowFeedDataEntry.CODEC.codec(), DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        DynamicRegistries.registerSynced(Keys.SIMPLE_DATA, SimpleData.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
    }
}
