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
                .add(Blocks.DIRT_PATH, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MUD, Blocks.HONEY_BLOCK);
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
    }
}
