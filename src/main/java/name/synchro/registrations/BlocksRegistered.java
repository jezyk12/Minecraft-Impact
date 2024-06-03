package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.blocks.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PlantBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_BASIC;
import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_DECORATION;
@SuppressWarnings("unused")
public final class BlocksRegistered {
    // Basic Blocks
    public static final MixedOre MIXED_ORE = registerBlock("mixed_ore",
            new MixedOre(FabricBlockSettings.of(Material.STONE).strength(0.1f)),SYNCHRO_BASIC);
    public static final CableBlock CABLE = registerBlock("cable",
            new CableBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f).nonOpaque()),SYNCHRO_BASIC);
    public static final ElectricSourceBlock ELECTRIC_SOURCE = registerBlock("electric_source",
            new ElectricSourceBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);
    public static final ElectricLampBlock ELECTRIC_LAMP = registerBlock("electric_lamp",
            new ElectricLampBlock(FabricBlockSettings.of(Material.METAL)
                    .luminance(state -> 15).strength(0.1f)),SYNCHRO_BASIC);
    public static final DebugBlock DEBUG_BLOCK = registerBlock("debug_block",
            new DebugBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);
    public static final DirtCanalBlock DIRT_CANAL = registerBlock("dirt_canal",
            new DirtCanalBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).dropsLike(Blocks.DIRT).sounds(BlockSoundGroup.ROOTED_DIRT)),SYNCHRO_BASIC);
    public static final BushWithFlowers BUSH_WITH_FLOWERS = registerFlammableBlock("bush_with_flowers",
            new BushWithFlowers(FabricBlockSettings.of(Material.GLASS).nonOpaque().noCollision().breakInstantly().dropsLike(Blocks.DEAD_BUSH).sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC, 60, 100);
    public static final PlantBlock BUSH = registerFlammableBlock("bush",
            new PlantBlock(FabricBlockSettings.of(Material.GLASS).nonOpaque().noCollision().breakInstantly().dropsLike(Blocks.DEAD_BUSH).sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC, 60, 100);
    public static final TomatoBushBlock TOMATO_BUSH = registerBlock("tomato_bush",
            new TomatoBushBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().ticksRandomly().sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC);
    public static final FertileDirtBlock FERTILE_DIRT = registerBlock("fertile_dirt",
            new FertileDirtBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).sounds(BlockSoundGroup.GRAVEL)),SYNCHRO_BASIC);
    public static final FertileFarmlandBlock FERTILE_FARMLAND = registerBlock("fertile_farmland",
            new FertileFarmlandBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).sounds(BlockSoundGroup.GRAVEL)),SYNCHRO_BASIC);
    public static final PeanutBushBlock PEANUT_BUSH_BLOCK = registerBlock("peanut_bush",
            new PeanutBushBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).drops(new Identifier(Synchro.MOD_ID, "blocks/peanut_bush"))), SYNCHRO_BASIC);
    public static final BananaStem BANANA_STEM = registerFlammableBlock("banana_stem",
            new BananaStem(FabricBlockSettings.of(Material.LEAVES).strength(0.1f).sounds(BlockSoundGroup.STEM)), SYNCHRO_BASIC, 5, 5);
    public static final BananaLeavesBlock BANANA_LEAVES = registerFlammableBlock("banana_leaves",
            new BananaLeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.1f).nonOpaque().sounds(BlockSoundGroup.GRASS)), SYNCHRO_BASIC, 30, 60);
    public static final BananaBlock BANANA_BLOCK = registerBlock("banana_block",
            new BananaBlock(FabricBlockSettings.of(Material.LEAVES).sounds(BlockSoundGroup.WOOD).breakInstantly().nonOpaque()), SYNCHRO_BASIC);
    public static final Block PLANT_FIBRE_BLOCK = registerBlock("plant_fibre_block",
            new Block(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.STEM).strength(0.1f)), SYNCHRO_BASIC);
    public static final Millstone MILLSTONE = registerBlock("millstone",
            new Millstone(FabricBlockSettings.of(Material.STONE).strength(2.0f).nonOpaque().sounds(BlockSoundGroup.STONE)), SYNCHRO_BASIC);
    public static final StrawNestBlock STRAW_NEST = registerBlock("straw_nest",
            new StrawNestBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).strength(0.1f).sounds(BlockSoundGroup.GRASS)), SYNCHRO_BASIC);

    //Gases
    public static final GasBlock WATER_VAPOR_BLOCK = registerBlock("water_vapor",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.WATER_VAPOR_GAS), SYNCHRO_BASIC);
    public static final GasBlock HOT_STEAM_BLOCK = registerBlock("hot_steam",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.HOT_STEAM_GAS), SYNCHRO_BASIC);
    public static final GasBlock SULFURIC_GAS_BLOCK = registerBlock("sulfuric_gas",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.SULFURIC_GAS), SYNCHRO_BASIC);
    public static final GasBlock CHLORIC_GAS_BLOCK = registerBlock("chloric_gas",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.CHLORIC_GAS), SYNCHRO_BASIC);
    public static final GasBlock STRONGLY_REDUCING_GAS_BLOCK = registerBlock("strongly_reducing_gas",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.STRONGLY_REDUCING_GAS), SYNCHRO_BASIC);
    public static final GasBlock STRONGLY_OXIDIZING_GAS_BLOCK = registerBlock("strongly_oxidizing_gas",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.STRONGLY_OXIDIZING_GAS), SYNCHRO_BASIC);

    // Slope Blocks
    public static final SlopeBlock OAK_PLANKS_SLOPE = registerFlammableBlock("oak_planks_slope",
            new SlopeBlock(Blocks.OAK_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock SPRUCE_PLANKS_SLOPE = registerFlammableBlock("spruce_planks_slope",
            new SlopeBlock(Blocks.SPRUCE_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock BIRCH_PLANKS_SLOPE = registerFlammableBlock("birch_planks_slope",
            new SlopeBlock(Blocks.BIRCH_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock JUNGLE_PLANKS_SLOPE = registerFlammableBlock("jungle_planks_slope",
            new SlopeBlock(Blocks.JUNGLE_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock ACACIA_PLANKS_SLOPE = registerFlammableBlock("acacia_planks_slope",
            new SlopeBlock(Blocks.ACACIA_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock DARK_OAK_PLANKS_SLOPE = registerFlammableBlock("dark_oak_planks_slope",
            new SlopeBlock(Blocks.DARK_OAK_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock CHERRY_PLANKS_SLOPE = registerFlammableBlock("cherry_planks_slope",
            new SlopeBlock(Blocks.CHERRY_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock BAMBOO_PLANKS_SLOPE = registerFlammableBlock("bamboo_planks_slope",
            new SlopeBlock(Blocks.BAMBOO_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD)), SYNCHRO_DECORATION, 5, 20);
    public static final SlopeBlock CRIMSON_PLANKS_SLOPE = registerBlock("crimson_planks_slope",
            new SlopeBlock(Blocks.CRIMSON_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.NETHER_WOOD)), SYNCHRO_DECORATION);
    public static final SlopeBlock WARPED_PLANKS_SLOPE = registerBlock("warped_planks_slope",
            new SlopeBlock(Blocks.WARPED_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.NETHER_WOOD)), SYNCHRO_DECORATION);
    public static final SlopeBlock STONE_SLOPE = registerBlock("stone_slope",
            new SlopeBlock(Blocks.STONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock GRANITE_SLOPE = registerBlock("granite_slope",
            new SlopeBlock(Blocks.GRANITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_GRANITE_SLOPE = registerBlock("polished_granite_slope",
            new SlopeBlock(Blocks.POLISHED_GRANITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock DIORITE_SLOPE = registerBlock("diorite_slope",
            new SlopeBlock(Blocks.DIORITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_DIORITE_SLOPE = registerBlock("polished_diorite_slope",
            new SlopeBlock(Blocks.POLISHED_DIORITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock ANDESITE_SLOPE = registerBlock("andesite_slope",
            new SlopeBlock(Blocks.ANDESITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_ANDESITE_SLOPE = registerBlock("polished_andesite_slope",
            new SlopeBlock(Blocks.POLISHED_ANDESITE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock COBBLESTONE_SLOPE = registerBlock("cobblestone_slope",
            new SlopeBlock(Blocks.COBBLESTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock MOSSY_COBBLESTONE_SLOPE = registerBlock("mossy_cobblestone_slope",
            new SlopeBlock(Blocks.MOSSY_COBBLESTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock STONE_BRICKS_SLOPE = registerBlock("stone_bricks_slope",
            new SlopeBlock(Blocks.STONE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock MOSSY_STONE_BRICKS_SLOPE = registerBlock("mossy_stone_bricks_slope",
            new SlopeBlock(Blocks.MOSSY_STONE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock BRICKS_SLOPE = registerBlock("bricks_slope",
            new SlopeBlock(Blocks.BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock END_STONE_BRICKS_SLOPE = registerBlock("end_stone_bricks_slope",
            new SlopeBlock(Blocks.END_STONE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock NETHER_BRICKS_SLOPE = registerBlock("nether_bricks_slope",
            new SlopeBlock(Blocks.NETHER_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock RED_NETHER_BRICKS_SLOPE = registerBlock("red_nether_bricks_slope",
            new SlopeBlock(Blocks.RED_NETHER_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock SANDSTONE_SLOPE = registerBlock("sandstone_slope",
            new SlopeBlock(Blocks.SANDSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock SMOOTH_SANDSTONE_SLOPE = registerBlock("smooth_sandstone_slope",
            new SlopeBlock(Blocks.SMOOTH_SANDSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock RED_SANDSTONE_SLOPE = registerBlock("red_sandstone_slope",
            new SlopeBlock(Blocks.RED_SANDSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock SMOOTH_RED_SANDSTONE_SLOPE = registerBlock("smooth_red_sandstone_slope",
            new SlopeBlock(Blocks.SMOOTH_RED_SANDSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock QUARTZ_BLOCK_SLOPE = registerBlock("quartz_block_slope",
            new SlopeBlock(Blocks.QUARTZ_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock SMOOTH_QUARTZ_SLOPE = registerBlock("smooth_quartz_slope",
            new SlopeBlock(Blocks.SMOOTH_QUARTZ.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock PURPUR_BLOCK_SLOPE = registerBlock("purpur_block_slope",
            new SlopeBlock(Blocks.PURPUR_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock PRISMARINE_SLOPE = registerBlock("prismarine_slope",
            new SlopeBlock(Blocks.PRISMARINE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock PRISMARINE_BRICKS_SLOPE = registerBlock("prismarine_bricks_slope",
            new SlopeBlock(Blocks.PRISMARINE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock DARK_PRISMARINE_SLOPE = registerBlock("dark_prismarine_slope",
            new SlopeBlock(Blocks.DARK_PRISMARINE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock BLACKSTONE_SLOPE = registerBlock("blackstone_slope",
            new SlopeBlock(Blocks.BLACKSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_BLACKSTONE_SLOPE = registerBlock("polished_blackstone_slope",
            new SlopeBlock(Blocks.POLISHED_BLACKSTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_BLACKSTONE_BRICKS_SLOPE = registerBlock("polished_blackstone_bricks_slope",
            new SlopeBlock(Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock COBBLED_DEEPSLATE_SLOPE = registerBlock("cobbled_deepslate_slope",
            new SlopeBlock(Blocks.COBBLED_DEEPSLATE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock POLISHED_DEEPSLATE_SLOPE = registerBlock("polished_deepslate_slope",
            new SlopeBlock(Blocks.POLISHED_DEEPSLATE.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock DEEPSLATE_TILES_SLOPE = registerBlock("deepslate_tiles_slope",
            new SlopeBlock(Blocks.DEEPSLATE_TILES.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock MUD_BRICKS_SLOPE = registerBlock("mud_bricks_slope",
            new SlopeBlock(Blocks.MUD_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock TUFF_SLOPE = registerBlock("tuff_slope",
            new SlopeBlock(Blocks.TUFF.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock DEEPSLATE_BRICKS_SLOPE = registerBlock("deepslate_bricks_slope",
            new SlopeBlock(Blocks.DEEPSLATE_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE)), SYNCHRO_DECORATION);
    public static final SlopeBlock CUT_COPPER_SLOPE = registerBlock("cut_copper_slope",
            new SlopeBlock(Blocks.CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock EXPOSED_CUT_COPPER_SLOPE = registerBlock("exposed_cut_copper_slope",
            new SlopeBlock(Blocks.EXPOSED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock WEATHERED_CUT_COPPER_SLOPE = registerBlock("weathered_cut_copper_slope",
            new SlopeBlock(Blocks.WEATHERED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock OXIDIZED_CUT_COPPER_SLOPE = registerBlock("oxidized_cut_copper_slope",
            new SlopeBlock(Blocks.OXIDIZED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock WAXED_CUT_COPPER_SLOPE = registerBlock("waxed_cut_copper_slope",
            new SlopeBlock(Blocks.WAXED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock WAXED_EXPOSED_CUT_COPPER_SLOPE = registerBlock("waxed_exposed_cut_copper_slope",
            new SlopeBlock(Blocks.WAXED_EXPOSED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock WAXED_WEATHERED_CUT_COPPER_SLOPE = registerBlock("waxed_weathered_cut_copper_slope",
            new SlopeBlock(Blocks.WAXED_WEATHERED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);
    public static final SlopeBlock WAXED_OXIDIZED_CUT_COPPER_SLOPE = registerBlock("waxed_oxidized_cut_copper_slope",
            new SlopeBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER.getDefaultState(), FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()), SYNCHRO_DECORATION);

    // Ores
    public static final OresRockBlock SEKITE = registerBlock("sekite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock AOITE = registerBlock("aoite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock MIDORITE = registerBlock("midorite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock MURAXKITE = registerBlock("muraxkite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock GUMITE = registerBlock("gumite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock NGANITE = registerBlock("nganite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock HAAKITE = registerBlock("haakite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final OresRockBlock BAAKITE = registerBlock("baakite",
            new OresRockBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_COARSE = registerBlock("rock_coarse",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_CRACKED = registerBlock("rock_cracked",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_DARK = registerBlock("rock_dark",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_LIGHT = registerBlock("rock_light",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_SCRATCH = registerBlock("rock_scratch",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_SHINY = registerBlock("rock_shiny",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_SMOOTH = registerBlock("rock_smooth",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);
    public static final Rock ROCK_STRATIFORM = registerBlock("rock_stratiform",
            new Rock(FabricBlockSettings.of(Material.STONE).strength(1.0f)), SYNCHRO_BASIC);

    // Others

    private static void registerAllBlocksAboutCopper(){
        OxidizableBlocksRegistry.registerOxidizableBlockPair(CUT_COPPER_SLOPE, EXPOSED_CUT_COPPER_SLOPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_CUT_COPPER_SLOPE, WEATHERED_CUT_COPPER_SLOPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_CUT_COPPER_SLOPE, OXIDIZED_CUT_COPPER_SLOPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WAXED_OXIDIZED_CUT_COPPER_SLOPE, WAXED_WEATHERED_CUT_COPPER_SLOPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WAXED_WEATHERED_CUT_COPPER_SLOPE, WAXED_EXPOSED_CUT_COPPER_SLOPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WAXED_EXPOSED_CUT_COPPER_SLOPE, WAXED_CUT_COPPER_SLOPE);
    }

    private static <T extends Block> T registerBlock(String path, T block){
        ItemsRegistered.registerItem(path, new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, new Identifier(Synchro.MOD_ID, path), block);
    }

    private static <T extends Block> T registerBlock(String path, T block, ItemGroup itemGroup){
        ItemsRegistered.registerItem(path, new BlockItem(block, new FabricItemSettings()), itemGroup);
        return Registry.register(Registries.BLOCK, new Identifier(Synchro.MOD_ID, path), block);
    }

    private static <T extends Block> T registerFlammableBlock(String path, T block, ItemGroup itemGroup, int burn, int spread){
        ItemsRegistered.registerItem(path, new BlockItem(block,new FabricItemSettings()), itemGroup);
        T self = Registry.register(Registries.BLOCK, new Identifier(Synchro.MOD_ID, path), block);
        FlammableBlockRegistry.getInstance(Blocks.FIRE).add(self, burn, spread);
        return self;
    }

    public static void registerAll() {
        registerAllBlocksAboutCopper();
        Synchro.LOGGER.debug("Registered mod blocks for" + Synchro.MOD_ID);
    }
}
