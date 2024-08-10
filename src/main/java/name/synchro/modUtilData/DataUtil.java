package name.synchro.modUtilData;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import name.synchro.registrations.ModRegistries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.stream.Stream;

public final class DataUtil {
    public static Stream<FluidReaction> getFluidReactionsStream(RegistryWrapper.WrapperLookup lookup){
        try {
            return lookup.getWrapperOrThrow(ModRegistries.Keys.FLUID_REACTION).streamEntries().map(RegistryEntry.Reference::value);
        } catch (Exception e){
            Synchro.LOGGER.error("Unable to get fluidReactions: {}", e, e);
            return Stream.<FluidReaction>builder().build();
        }
    }

    public static RegistryEntry<Enchantment> entryOf(RegistryKey<Enchantment> key, RegistryWrapper.WrapperLookup wrapperLookup) {
        return wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key);
    }

}
