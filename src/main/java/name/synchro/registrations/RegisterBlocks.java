package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.synchroBlocks.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_BASIC;

public class RegisterBlocks {
    public static final MixedOre MIXED_ORE = registerBlock("mixed_ore",
            new MixedOre(FabricBlockSettings.of(Material.STONE).strength(0.1f)),SYNCHRO_BASIC);
    public static final SlopeBlock WOODEN_SLOPE = registerBlock("wooden_slope",
            new SlopeBlock(Blocks.OAK_PLANKS.getDefaultState(), FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.1f).nonOpaque()), SYNCHRO_BASIC);
    public static final CableBlock CABLE = registerBlock("cable",
            new CableBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f).nonOpaque()),SYNCHRO_BASIC);
    public static final ElectricSourceBlock ELECTRIC_SOURCE = registerBlock("electric_source",
            new ElectricSourceBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);
    public static final ElectricLampBlock ELECTRIC_LAMP = registerBlock("electric_lamp",
            new ElectricLampBlock(FabricBlockSettings.of(Material.METAL)
                    .luminance(createLightLevelFromLitBlockState(15)).strength(0.1f)),SYNCHRO_BASIC);
    public static final DebugBlock DEBUG_BLOCK = registerBlock("debug_block",
            new DebugBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);
    public static final DirtCanalBlock DIRT_CANAL = registerBlock("dirt_canal",
            new DirtCanalBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).dropsLike(Blocks.DIRT).sounds(BlockSoundGroup.ROOTED_DIRT)),SYNCHRO_BASIC);
    public static final BushWithFlowers BUSH_WITH_FLOWERS = registerBlock("bush_with_flowers",
            new BushWithFlowers(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().dropsLike(Blocks.DEAD_BUSH).sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC);
    public static final PlantBlock BUSH = registerBlock("bush",
            new PlantBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().dropsLike(Blocks.DEAD_BUSH).sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC);
    public static final TomatoBushBlock TOMATO_BUSH = registerBlock("tomato_bush",
            new TomatoBushBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().ticksRandomly().sounds(BlockSoundGroup.GRASS)),SYNCHRO_BASIC);
    public static final FertileDirtBlock FERTILE_DIRT = registerBlock("fertile_dirt",
            new FertileDirtBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).sounds(BlockSoundGroup.GRAVEL)),SYNCHRO_BASIC);
    public static final FertileFarmlandBlock FERTILE_FARMLAND = registerBlock("fertile_farmland",
            new FertileFarmlandBlock(FabricBlockSettings.of(Material.SOIL).strength(0.1f).sounds(BlockSoundGroup.GRAVEL)),SYNCHRO_BASIC);
    public static final GasBlock WATER_VAPOR_BLOCK = registerBlock("water_vapor",
            new GasBlock(FabricBlockSettings.of(Material.AIR).noCollision().nonOpaque().dropsNothing(),RegisterFluids.WATER_VAPOR_GAS),SYNCHRO_BASIC);
    public static final PeanutBushBlock PEANUT_BUSH_BLOCK = registerBlock("peanut_bush",
            new PeanutBushBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).drops(new Identifier(Synchro.MOD_ID, "blocks/peanut_bush"))), SYNCHRO_BASIC);
    public static final BananaStem BANANA_STEM = registerBlock("banana_stem",
            new BananaStem(FabricBlockSettings.of(Material.PLANT).strength(0.1f).sounds(BlockSoundGroup.STEM)), SYNCHRO_BASIC);
    public static final BananaLeavesBlock BANANA_LEAVES = registerBlock("banana_leaves",
            new BananaLeavesBlock(FabricBlockSettings.of(Material.PLANT).strength(0.1f).nonOpaque().sounds(BlockSoundGroup.GRASS)), SYNCHRO_BASIC);
    public static final BananaBlock BANANA_BLOCK = registerBlock("banana_block",
            new BananaBlock(FabricBlockSettings.of(Material.PLANT).sounds(BlockSoundGroup.WOOD).breakInstantly().nonOpaque()), SYNCHRO_BASIC);


    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
    private static <T extends Block> T registerBlock(String path, T block, ItemGroup itemGroup){
        RegisterItems.registerItem(path, new BlockItem(block,new FabricItemSettings()), itemGroup);
        return Registry.register(Registries.BLOCK, new Identifier(Synchro.MOD_ID, path), block);
    }
    public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod blocks for" + Synchro.MOD_ID);
    }
}
