package name.synchro.util.dataDriven;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public final class DataUtil {
    public static RegistryEntry<Enchantment> entryOf(RegistryKey<Enchantment> key, RegistryWrapper.WrapperLookup wrapperLookup) {
        return wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key);
    }
}
