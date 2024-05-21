package name.synchro.dataGeneration;

import name.synchro.Synchro;
import name.synchro.registrations.RegisterBlocks;
import name.synchro.registrations.RegisterItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RecipesData extends FabricRecipeProvider {

    public RecipesData(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, RegisterItems.PLANT_FIBRE, 2)
                .input(RegisterItems.BANANA).
                criterion("has_banana", RecipeProvider.conditionsFromItem(RegisterItems.BANANA))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "plant_fibre_from_banana"));
        VanillaRecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(
                exporter, RecipeCategory.MISC, RegisterItems.PLANT_FIBRE, RecipeCategory.BUILDING_BLOCKS, RegisterBlocks.PLANT_FIBRE_BLOCK,
                "plant_fibre_from_its_block", "plant_fibre");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_MUD, 4)
                .input(Blocks.DIRT, 4).input(RegisterItems.PLANT_FIBRE, 4).input(Items.WATER_BUCKET)
                .criterion("has_plant_fibre", RecipeProvider.conditionsFromItem(RegisterItems.PLANT_FIBRE))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "packed_mud_from_dirt"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD, 8)
                .input(Blocks.DIRT, 8).input(Items.WATER_BUCKET)
                .criterion("has_dirt", RecipeProvider.conditionsFromItem(Blocks.DIRT))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "mud_from_dirt"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_MUD, 1)
                .input(Blocks.MUD, 1).input(RegisterItems.PLANT_FIBRE, 1)
                .criterion("has_plant_fibre", RecipeProvider.conditionsFromItem(RegisterItems.PLANT_FIBRE))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "packed_mud_from_mud"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.CLAY, 8)
                .input(Blocks.MUD, 8).input(ItemTags.SAND)
                .criterion("has_mud", RecipeProvider.conditionsFromItem(Blocks.MUD))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "clay_from_mud"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, RegisterItems.FRESH_FORAGE, 4)
                .pattern("abc").pattern("bcb").pattern("cba")
                .input('a', Items.SWEET_BERRIES).input('b', Ingredient.ofItems(Items.GRASS, Items.FERN)).input('c', Items.WHEAT)
                .criterion("has_wheat", RecipeProvider.conditionsFromItem(Items.WHEAT))
                .offerTo(exporter, new Identifier(Synchro.MOD_ID, "fresh_forage"));
    }
}
