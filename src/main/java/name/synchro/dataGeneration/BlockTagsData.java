package name.synchro.dataGeneration;

import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public final class BlockTagsData extends FabricTagProvider.BlockTagProvider {

    public BlockTagsData(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        extendVanillaTags();
        createBurnableBlocks();
        getOrCreateTagBuilder(ModTags.CAN_STORE_FLUID)
                .addOptionalTag(BlockTags.LEAVES)
                .addOptionalTag(BlockTags.IMPERMEABLE)
                .add(Blocks.MANGROVE_ROOTS);

        FabricTagBuilder destroy_in_water = getOrCreateTagBuilder(ModTags.DESTROY_IN_WATER)
                .add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH)
                .add(Blocks.LILY_PAD, Blocks.SNOW, Blocks.POWDER_SNOW)
                .addOptionalTag(BlockTags.FIRE)
                .addOptionalTag(BlockTags.CANDLES);

        FabricTagBuilder tag_WashAwayByWater = getOrCreateTagBuilder(ModTags.WASH_AWAY_BY_WATER);
        addVanillaSmallRedstoneComponents(tag_WashAwayByWater);
        addUnstableNonburnable(tag_WashAwayByWater);
        addUnstableBurnables(tag_WashAwayByWater);

        FabricTagBuilder burn_away_by_lava = getOrCreateTagBuilder(ModTags.BURN_AWAY_IN_LAVA)
                .addOptionalTag(BlockTags.SAPLINGS);
        addVanillaSmallRedstoneComponents(burn_away_by_lava);
        addUnstableBurnables(burn_away_by_lava);

        FabricTagBuilder tag_WashAwayByLava = getOrCreateTagBuilder(ModTags.WASH_AWAY_BY_LAVA)
                .addOptionalTag(ModTags.BURN_AWAY_IN_LAVA);;
        addUnstableNonburnable(tag_WashAwayByLava);

        getOrCreateTagBuilder(ModTags.NEVER_FILL_FLUID)
                .add(Blocks.DIRT_PATH, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MUD, Blocks.HONEY_BLOCK)
                .add(Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.END_GATEWAY);
    }

    private static void addVanillaSmallRedstoneComponents(FabricTagBuilder builder){
        builder.add(Blocks.REDSTONE_WIRE, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.REPEATER)
                .add( Blocks.COMPARATOR, Blocks.LEVER, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK).addOptionalTag(BlockTags.BUTTONS);
    }

    private static void addUnstableNonburnable(FabricTagBuilder builder){
        builder.add(Blocks.WEEPING_VINES, Blocks.TWISTING_VINES, Blocks.WEEPING_VINES_PLANT, Blocks.TWISTING_VINES_PLANT)
                .add(Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS,Blocks.NETHER_WART)
                .add(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS, Blocks.NETHER_SPROUTS);
    }

    private static void addUnstableBurnables(FabricTagBuilder builder){
        builder.add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH)
                .add(Blocks.SWEET_BERRY_BUSH, Blocks.SUGAR_CANE, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM)
                .add(Blocks.SNOW, Blocks.POWDER_SNOW, Blocks.SCULK_VEIN, Blocks.MOSS_CARPET)
                .add(Blocks.COBWEB, Blocks.FROGSPAWN, Blocks.SPORE_BLOSSOM, Blocks.DEAD_BUSH)
                .add(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN)
                .add(Blocks.LILY_PAD, Blocks.VINE, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS)
                .add(Blocks.TURTLE_EGG)
                .addOptionalTag(BlockTags.CANDLES)
                .addOptionalTag(BlockTags.WOOL_CARPETS)
                .addOptionalTag(BlockTags.CAVE_VINES)
                .addOptionalTag(BlockTags.CROPS)
                .addOptionalTag(BlockTags.FLOWERS);
    }

    private void extendVanillaTags(){
        getOrCreateTagBuilder(BlockTags.FENCES).add(ModBlocks.BURNT_CHARCOAL_FENCE);
        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(ModBlocks.BURNT_CHARCOAL_FENCE);
        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(ModBlocks.BURNT_CHARCOAL_SLAB);
        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(ModBlocks.BURNT_CHARCOAL_STAIRS);
        getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES).add(ModBlocks.BURNT_CHARCOAL_PRESSURE_PLATE);
    }

    private void createBurnableBlocks(){
        getOrCreateTagBuilder(ModTags.BURNABLE_FENCE)
                .add(Blocks.ACACIA_FENCE, Blocks.BIRCH_FENCE, Blocks.DARK_OAK_FENCE, Blocks.JUNGLE_FENCE,
                        Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.MANGROVE_FENCE, Blocks.BAMBOO_FENCE,
                        Blocks.CHERRY_FENCE);
        getOrCreateTagBuilder(ModTags.BURNABLE_SLAB)
                .add(Blocks.ACACIA_SLAB, Blocks.BIRCH_SLAB, Blocks.DARK_OAK_SLAB, Blocks.JUNGLE_SLAB,
                        Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.MANGROVE_SLAB, Blocks.BAMBOO_SLAB,
                        Blocks.CHERRY_SLAB, Blocks.BAMBOO_MOSAIC_SLAB);
        getOrCreateTagBuilder(ModTags.BURNABLE_STAIRS)
                .add(Blocks.ACACIA_STAIRS, Blocks.BIRCH_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.JUNGLE_STAIRS,
                        Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.MANGROVE_STAIRS, Blocks.BAMBOO_STAIRS,
                        Blocks.CHERRY_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS);
        getOrCreateTagBuilder(ModTags.BURNABLE_PRESSURE_PlATE)
                .add(Blocks.ACACIA_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE,
                        Blocks.JUNGLE_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE,
                        Blocks.MANGROVE_PRESSURE_PLATE, Blocks.BAMBOO_PRESSURE_PLATE, Blocks.CHERRY_PRESSURE_PLATE);
        getOrCreateTagBuilder(ModTags.BURNABLE_SLOPE)
                .add(ModBlocks.ACACIA_SLOPE, ModBlocks.BIRCH_SLOPE, ModBlocks.DARK_OAK_SLOPE, ModBlocks.JUNGLE_SLOPE,
                        ModBlocks.OAK_SLOPE, ModBlocks.SPRUCE_SLOPE, ModBlocks.BAMBOO_SLOPE, ModBlocks.CHERRY_SLOPE,
                        ModBlocks.BAMBOO_MOSAIC_SLOPE, ModBlocks.MANGROVE_SLOPE);
    }
}
