package name.synchro.specialRecipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import name.synchro.Synchro;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public final class MillstoneRecipeSerializer_v0 implements RecipeSerializer<MillstoneRecipe> {
    public static final MillstoneRecipeSerializer_v0 INSTANCE = new MillstoneRecipeSerializer_v0();
    public static final Identifier ID = new Identifier(Synchro.MOD_ID, "millstone");
    private static final Gson GSON = new Gson();

    @Override
    public MillstoneRecipe read(Identifier id, JsonObject json) {
        JsonFormat recipeJson = GSON.fromJson(json, JsonFormat.class);
        Ingredient input = Ingredient.fromJson(Objects.requireNonNull(recipeJson.input, "Invalid input!"));
        Item outputItem = Objects.requireNonNull(Registries.ITEM.get(new Identifier(recipeJson.output)), "Invalid output!");
        ItemStack output = new ItemStack(outputItem, Math.min(Math.max(recipeJson.amount, 1), 64));
        int degrees = Math.max(1, recipeJson.degrees);
        return new MillstoneRecipe(id, input, output, degrees, recipeJson.copyNbt);
    }

    @Override
    public MillstoneRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        int degrees = buf.readInt();
        boolean copyNbt = buf.readBoolean();
        return new MillstoneRecipe(id, input, output, degrees, copyNbt);
    }

    @Override
    public void write(PacketByteBuf buf, MillstoneRecipe recipe) {
        recipe.input.write(buf);
        buf.writeItemStack(recipe.output);
        buf.writeInt(recipe.degrees);
        buf.writeBoolean(recipe.copyNbt);
    }

    public static class JsonFormat {
        JsonElement input;
        String output;
        int amount;
        int degrees;
        boolean copyNbt;
    }
}
