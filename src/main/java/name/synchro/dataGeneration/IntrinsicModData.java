package name.synchro.dataGeneration;

import com.mojang.datafixers.util.Pair;
import name.synchro.api.ModRegistryProvider;
import name.synchro.blocks.SlopeBlock;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import name.synchro.modUtilData.locationAction.ActiveEvents;
import name.synchro.modUtilData.locationAction.SetBlock;
import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModItems;
import name.synchro.registrations.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.WorldEvents;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class IntrinsicModData extends ModRegistryProvider {
    public IntrinsicModData(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        addAllFluidReactions(entries);
        addAllCowWorkingFeedsData(entries);
    }

    private static void addAllFluidReactions(Entries entries) {
        addLavaReactions(entries, "wooden_slab_down", ModTags.BURNABLE_SLAB, ModBlocks.BURNT_CHARCOAL_SLAB, 3,
                StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.BOTTOM));
        addLavaReactions(entries, "wooden_slab_up", ModTags.BURNABLE_SLAB, ModBlocks.BURNT_CHARCOAL_SLAB, 6,
                StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.TOP));
        addLavaReactions(entries, "wooden_stairs_down", ModTags.BURNABLE_STAIRS, ModBlocks.BURNT_CHARCOAL_STAIRS, 3,
                StatePredicate.Builder.create().exactMatch(StairsBlock.HALF, BlockHalf.BOTTOM));
        addLavaReactions(entries, "wooden_stairs_top", ModTags.BURNABLE_STAIRS, ModBlocks.BURNT_CHARCOAL_STAIRS, 6,
                StatePredicate.Builder.create().exactMatch(StairsBlock.HALF, BlockHalf.TOP));
        addLavaReactions(entries, "wooden_fence", ModTags.BURNABLE_FENCE, ModBlocks.BURNT_CHARCOAL_FENCE, 4,
                StatePredicate.Builder.create());
        addLavaReactions(entries, "wooden_pressure_plate", ModTags.BURNABLE_PRESSURE_PlATE, ModBlocks.BURNT_CHARCOAL_PRESSURE_PLATE, 1,
                StatePredicate.Builder.create());
        addLavaReactions(entries, "wooden_slope_down", ModTags.BURNABLE_SLOPE, ModBlocks.BURNT_CHARCOAL_SLOPE, 3,
                StatePredicate.Builder.create().exactMatch(SlopeBlock.HALF, BlockHalf.BOTTOM));
        addLavaReactions(entries, "wooden_slope_top", ModTags.BURNABLE_SLOPE, ModBlocks.BURNT_CHARCOAL_SLOPE, 6,
                StatePredicate.Builder.create().exactMatch(SlopeBlock.HALF, BlockHalf.TOP));
    }

    private static void addLavaReactions(Entries entries, String name, TagKey<Block> tagKey, Block result, int minLevel, StatePredicate.Builder blockPredicate) {
        addFluidReaction(entries,  "burn_" + name + "_flowing", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.FLOWING_LAVA).state(fluidLevelBetween(minLevel, null)).build())
                .block(BlockPredicate.Builder.create().tag(tagKey).state(blockPredicate).build())
                .action(SetBlock.builder(result.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_" + name + "_still", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.LAVA).build())
                .block(BlockPredicate.Builder.create().tag(tagKey).state(blockPredicate).build())
                .action(SetBlock.builder(result.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
    }

    private static void addAllCowWorkingFeedsData(Entries entries){
        addCowWorkingFeedsData(entries, "default_7600",
                new CowFeedDataEntry(Ingredient.ofItems(Items.CAKE, Items.HAY_BLOCK, Items.ENCHANTED_GOLDEN_APPLE), 7600));
        addCowWorkingFeedsData(entries, "default_3600",
                new CowFeedDataEntry(Ingredient.ofItems(ModItems.FRESH_FORAGE, Items.GOLDEN_APPLE), 3600));
        addCowWorkingFeedsData(entries, "default_800",
                new CowFeedDataEntry(Ingredient.fromTag(ItemTags.COW_FOOD), 800));
        addCowWorkingFeedsData(entries, "default_300",
                new CowFeedDataEntry(Ingredient.ofItems(Items.SHORT_GRASS, Items.FERN), 300));
    }
}
