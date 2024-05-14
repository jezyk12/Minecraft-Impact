package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.items.*;
import name.synchro.util.Metals;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Map;

import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_BASIC;
@SuppressWarnings("unused")
public class RegisterItems {
    public static final RawMixedOre RAW_MIXED_ORE = registerItem("raw_mixed_ore",
            new RawMixedOre(new FabricItemSettings()),SYNCHRO_BASIC);
    public static final Item CABLE_ITEM = registerItem("cable_item",
            new BlockItem(RegisterBlocks.CABLE,new FabricItemSettings()),SYNCHRO_BASIC);
    public static final DataRod DATA_ROD = registerItem("data_rod",
            new DataRod(new FabricItemSettings().rarity(Rarity.EPIC)),SYNCHRO_BASIC);
    public static final Item UNIVERSAL_METER = registerItem("universal_meter",
            new Item(new FabricItemSettings().maxCount(1)),SYNCHRO_BASIC);
    public static final Item TOMATO = registerItem("tomato",
            new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).build())),SYNCHRO_BASIC);
    public static final Cockroach COCKROACH = registerItem("cockroach",
            new Cockroach(new FabricItemSettings().maxCount(16)),SYNCHRO_BASIC);
    public static final Item PEANUT = registerItem("peanut",
            new AliasedBlockItem(RegisterBlocks.PEANUT_BUSH_BLOCK, new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build())),SYNCHRO_BASIC);
    public static final Item BANANA = registerItem("banana",
            new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.8f).build())), SYNCHRO_BASIC);
    public static final Item PLANT_FIBRE = registerItem("plant_fibre",
            new Item(new FabricItemSettings()), SYNCHRO_BASIC);
    public static final SnowballLauncher SNOWBALL_LAUNCHER = registerItem("snowball_launcher",
            new SnowballLauncher(new FabricItemSettings().maxDamage(256).rarity(Rarity.RARE)), SYNCHRO_BASIC);
    public static final SpawnEggItem DUCK_SPAWN_EGG = registerItem("duck_spawn_egg",
            new SpawnEggItem(RegisterEntities.DUCK, 0xF4D03F, 0x273746, new FabricItemSettings()), SYNCHRO_BASIC);
    public static final OresMixture LUMP_ORES = registerItem("lump_ores",
            new OresMixture(new FabricItemSettings()));
    public static final OresMixture CRACKED_ORES = registerItem("cracked_ores",
            new OresMixture(new FabricItemSettings()));
    public static final OresMixture CRUSHED_ORES = registerItem("crushed_ores",
            new OresMixture(new FabricItemSettings()));

    protected static <T extends Item> T registerItem(String path, T item, ItemGroup itemGroup) {
        T registeredItem = Registry.register(
                Registries.ITEM, new Identifier(Synchro.MOD_ID, path), item);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(registeredItem));
        return registeredItem;
    }

    protected static <T extends Item> T registerItem(String path, T item) {
        return Registry.register(
                Registries.ITEM, new Identifier(Synchro.MOD_ID, path), item);
    }

    private static void addItemsToItemGroup(){
        NbtCompound emptyOresNbt = new NbtCompound();
        Metals.writeMetalContentToNbt(emptyOresNbt, Map.of());
        ItemStack emptyCrackedOresStack = new ItemStack(CRACKED_ORES);
        ItemStack emptyLumpOresStack = new ItemStack(LUMP_ORES);
        ItemStack emptyCrushedOresStack = new ItemStack(CRUSHED_ORES);
        emptyCrackedOresStack.setNbt(emptyOresNbt.copy());
        emptyCrushedOresStack.setNbt(emptyOresNbt.copy());
        emptyLumpOresStack.setNbt(emptyOresNbt.copy());
        ItemGroupEvents.modifyEntriesEvent(SYNCHRO_BASIC).register(entries -> {
            entries.add(emptyLumpOresStack);
            entries.add(emptyCrackedOresStack);
            entries.add(emptyCrushedOresStack);
        });
    }

    public static void registerAll(){
        addItemsToItemGroup();
        Synchro.LOGGER.debug("Registered mod items for"+Synchro.MOD_ID);
    }

}
