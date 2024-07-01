package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.specialRecipes.MillstoneRecipe;
import name.synchro.specialRecipes.MillstoneRecipeSerializer;
import name.synchro.specialRecipes.MixOreRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class RegisterRecipes {
    public static final RecipeSerializer<MixOreRecipe> MIX_ORE_RECIPE =
            RecipeSerializer.register("mix_ore_recipe", new SpecialRecipeSerializer<>(MixOreRecipe::new));
    public static final RecipeSerializer<MillstoneRecipe> MILLING_RECIPE =
            Registry.register(Registries.RECIPE_SERIALIZER, MillstoneRecipeSerializer.ID, MillstoneRecipeSerializer.INSTANCE);
    public static final RecipeType<MillstoneRecipe> MILLING_RECIPE_TYPE =
            Registry.register(Registries.RECIPE_TYPE, MillstoneRecipe.Type.ID, MillstoneRecipe.MILLING_RECIPE_TYPE);

    public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod recipes for" + Synchro.MOD_ID);
    }
}
