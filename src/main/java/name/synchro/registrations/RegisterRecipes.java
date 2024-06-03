package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.specialRecipes.MixOreRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;

public final class RegisterRecipes {
    public static final RecipeSerializer<MixOreRecipe> MIX_ORE_RECIPE =
            RecipeSerializer.register("mix_ore_recipe", new SpecialRecipeSerializer<>(MixOreRecipe::new));

    public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod recipes for" + Synchro.MOD_ID);
    }
}
