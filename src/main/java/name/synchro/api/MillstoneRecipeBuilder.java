package name.synchro.api;

import name.synchro.specialRecipes.MillstoneRecipes;
import net.minecraft.item.ItemConvertible;

/**
 * A builder for creating millstone recipes.
 */
public final class MillstoneRecipeBuilder extends MillstoneRecipes.Builder {
    public static MillstoneRecipeBuilder of(ItemConvertible item) {
        MillstoneRecipeBuilder builder = new MillstoneRecipeBuilder();
        builder.item = item.asItem();
        builder.conditions = stack -> stack.isOf(item.asItem());
        return builder;
    }
}
