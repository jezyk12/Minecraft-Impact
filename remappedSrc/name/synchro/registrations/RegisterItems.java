package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.synchroItems.DataRod;
import name.synchro.synchroItems.RawMixedOre;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_BASIC;

public class RegisterItems {
    public static final RawMixedOre MIXED_RAW_ORE = registerItem("raw_mixed_ore",
            new RawMixedOre(new FabricItemSettings()),SYNCHRO_BASIC);
    public static final Item CABLE_ITEM = registerItem("cable_item",
            new BlockItem(RegisterBlocks.CABLE,new FabricItemSettings()),SYNCHRO_BASIC);
    public static final DataRod DATA_ROD = registerItem("data_rod",
            new DataRod(new FabricItemSettings().rarity(Rarity.EPIC)),SYNCHRO_BASIC);
    public static final Item UNIVERSAL_METER = registerItem("universal_meter",
            new Item(new FabricItemSettings().maxCount(1)),SYNCHRO_BASIC);
    protected static <T extends Item> T registerItem(String path, T item, ItemGroup itemGroup) {
        T registeredItem = Registry.register(
                Registries.ITEM, new Identifier(Synchro.MOD_ID, path), item);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(registeredItem));
        return registeredItem;
    }
    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod items for"+Synchro.MOD_ID);
    }
}
