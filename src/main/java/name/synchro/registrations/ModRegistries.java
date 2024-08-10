package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.FluidReactionData;
import name.synchro.modUtilData.reactions.LocationAction;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ModRegistries {
    public static final class Keys{
        public static final RegistryKey<Registry<LocationAction.Type<?>>> LOCATION_ACTION_TYPE = RegistryKey.ofRegistry(Identifier.of(Synchro.MOD_ID, "location_action_type"));
        public static final RegistryKey<Registry<FluidReactionData.Entry>> FLUID_REACTION = RegistryKey.ofRegistry(Identifier.of(Synchro.MOD_ID, "fluid_reaction"));
    }

    public static final Registry<LocationAction.Type<?>> LOCATION_ACTION_TYPE =
            FabricRegistryBuilder.createSimple(Keys.LOCATION_ACTION_TYPE).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod registries for " + Synchro.MOD_ID);
        LocationAction.registerAll();
        DynamicRegistries.registerSynced(Keys.FLUID_REACTION, FluidReactionData.Entry.CODEC.codec(), DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
    }
}
