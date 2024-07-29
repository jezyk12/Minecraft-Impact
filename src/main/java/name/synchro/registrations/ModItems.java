package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.items.*;
import name.synchro.util.Metals;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Map;
import java.util.function.Supplier;

import static name.synchro.registrations.ModItemGroups.SYNCHRO_BASIC;
@SuppressWarnings("unused")
public final class ModItems {
    public static final RawMixedOre RAW_MIXED_ORE = registerItem("raw_mixed_ore",
            new RawMixedOre(new Item.Settings()),SYNCHRO_BASIC);
//    public static final Item CABLE_ITEM = registerItem("cable_item",
//            new BlockItem(ModBlocks.CABLE,new Item.Settings()),SYNCHRO_BASIC);
    public static final DataRod DATA_ROD = registerItem("data_rod",
            new DataRod(new Item.Settings().rarity(Rarity.EPIC)),SYNCHRO_BASIC);
//    public static final Item UNIVERSAL_METER = registerItem("universal_meter",
//            new Item(new Item.Settings().maxCount(1)),SYNCHRO_BASIC);
    public static final Item TOMATO = registerItem("tomato",
            new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.3f).build())),SYNCHRO_BASIC);
    public static final Cockroach COCKROACH = registerItem("cockroach",
            new Cockroach(new Item.Settings().maxCount(16)),SYNCHRO_BASIC);
    public static final Item PEANUT = registerItem("peanut",
            new AliasedBlockItem(ModBlocks.PEANUT_BUSH_BLOCK, new Item.Settings().food(new FoodComponent.Builder().nutrition(2).saturationModifier(0.2f).build())),SYNCHRO_BASIC);
    public static final Item BANANA = registerItem("banana",
            new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.8f).build())), SYNCHRO_BASIC);
    public static final Item PLANT_FIBRE = registerItem("plant_fibre",
            new Item(new Item.Settings()), SYNCHRO_BASIC);
    public static final SnowballLauncher SNOWBALL_LAUNCHER = registerItem("snowball_launcher",
            new SnowballLauncher(new Item.Settings().maxDamage(256).rarity(Rarity.RARE)), SYNCHRO_BASIC);
    public static final SpawnEggItem DUCK_SPAWN_EGG = registerItem("duck_spawn_egg",
            new SpawnEggItem(ModEntities.DUCK, 0xF4D03F, 0x273746, new Item.Settings()), SYNCHRO_BASIC);
    public static final OresMixture LUMP_ORES = registerItem("lump_ores",
            new OresMixture(new Item.Settings()));
    public static final OresMixture CRACKED_ORES = registerItem("cracked_ores",
            new OresMixture(new Item.Settings()));
    public static final OresMixture CRUSHED_ORES = registerItem("crushed_ores",
            new OresMixture(new Item.Settings()));
    public static final Item FRESH_FORAGE = registerItem("fresh_forage",
            new Item(new Item.Settings().maxCount(64)),SYNCHRO_BASIC);
    public static final OresMixture ORES_DUST = registerItem("ores_dust",
            new OresMixture(new Item.Settings()));

    static <T extends Item> T registerItem(String path, T item, RegistryKey<ItemGroup> itemGroupKey) {
        T registeredItem = Registry.register(
                Registries.ITEM, Identifier.of(Synchro.MOD_ID, path), item);
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(entries -> entries.add(registeredItem));
        return registeredItem;
    }

    static <T extends Item> T registerItem(String path, T item) {
        return Registry.register(
                Registries.ITEM, Identifier.of(Synchro.MOD_ID, path), item);
    }

    private static void addItemsToItemGroup(){
        NbtCompound emptyOresNbt = new NbtCompound();
        Metals.writeMetalContentToNbt(emptyOresNbt, Map.of());
        ItemStack emptyCrackedOresStack = new ItemStack(CRACKED_ORES);
        ItemStack emptyLumpOresStack = new ItemStack(LUMP_ORES);
        ItemStack emptyCrushedOresStack = new ItemStack(CRUSHED_ORES);
        ItemStack emptyOresDustStack = new ItemStack(ORES_DUST);
        setNbt(emptyCrackedOresStack, emptyOresNbt.copy());
        setNbt(emptyCrushedOresStack, emptyOresNbt.copy());
        setNbt(emptyLumpOresStack, emptyOresNbt.copy());
        ItemGroupEvents.modifyEntriesEvent(SYNCHRO_BASIC).register(entries -> {
            entries.add(emptyLumpOresStack);
            entries.add(emptyCrackedOresStack);
            entries.add(emptyCrushedOresStack);
            entries.add(emptyOresDustStack);
        });
    }

    public static void setNbt(ItemStack stack, NbtCompound nbt){
        stack.set(ModComponentTypes.MISC_NBT, NbtComponent.of(nbt));
    }

    public static boolean hasNbt(ItemStack stack){
        return getNbt(stack) != null;
    }

    public static NbtCompound getNbtOrDefault(ItemStack stack, Supplier<NbtCompound> supplier){
        NbtCompound nbtCompound = getNbt(stack);
        if (nbtCompound != null) return nbtCompound;
        else return supplier.get();
    }

    public static NbtCompound getNbt(ItemStack stack){
        NbtComponent nbtComponent = stack.getComponents().get(ModComponentTypes.MISC_NBT);
        if (nbtComponent != null){
            return nbtComponent.copyNbt();
        }
        else return null;
    }

    public static NbtElement encode(ItemStack stack, RegistryWrapper.WrapperLookup wrapperLookup){
        if (!stack.isEmpty()){
            return stack.encode(wrapperLookup);
        }
        else return new NbtCompound();
    }

    public static ItemStack decode(NbtElement nbtElement, RegistryWrapper.WrapperLookup wrapperLookup){
        return ItemStack.fromNbtOrEmpty(wrapperLookup, (NbtCompound) nbtElement);
    }

    public static void registerAll(){
        addItemsToItemGroup();
        Synchro.LOGGER.debug("Registered mod items for" + Synchro.MOD_ID);
    }

}
