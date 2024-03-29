package name.synchro.dataGeneration;

import name.synchro.Synchro;
import name.synchro.registrations.RegisterBlocks;
import name.synchro.registrations.RegisterItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RecipesData extends FabricRecipeProvider {

    public RecipesData(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, RegisterItems.PLANT_FIBRE, 2).input(RegisterItems.BANANA).criterion("has_banana", RecipeProvider.conditionsFromItem(RegisterItems.BANANA)).offerTo(exporter, new Identifier(Synchro.MOD_ID, "plant_fibre_from_banana"));
        VanillaRecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, RegisterItems.PLANT_FIBRE, RecipeCategory.BUILDING_BLOCKS, RegisterBlocks.PLANT_FIBRE_BLOCK, "plant_fibre_from_its_block", "plant_fibre");
    }
}
