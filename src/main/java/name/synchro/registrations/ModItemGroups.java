package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    public static RegistryKey<ItemGroup> SYNCHRO_BASIC =
            register(Identifier.of(Synchro.MOD_ID, "basic"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.synchro.synchro_basic"))
                    .icon(() -> new ItemStack(ModBlocks.BUSH)).build());
    public static RegistryKey<ItemGroup> SYNCHRO_DECORATION =
            register(Identifier.of(Synchro.MOD_ID, "decoration"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.synchro.synchro_decoration"))
                    .icon(() -> new ItemStack(ModBlocks.OAK_SLOPE)).build());

    private static RegistryKey<ItemGroup> register(Identifier id, ItemGroup itemGroup) {
        RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);
        Registry.register(Registries.ITEM_GROUP, key, itemGroup);
        return key;
    }

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod item groups for "+Synchro.MOD_ID);
    }
}
