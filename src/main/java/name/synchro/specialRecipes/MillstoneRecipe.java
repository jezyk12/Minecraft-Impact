package name.synchro.specialRecipes;

import name.synchro.Synchro;
import name.synchro.registrations.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record MillstoneRecipe(Ingredient input, ItemStack output, int degrees, boolean copyNbt)
        implements Recipe<MillstoneRecipe.Input> {
    public static final RecipeType<MillstoneRecipe> TYPE = new Type();

    public record Input(ItemStack stack) implements RecipeInput{
        @Override
        public ItemStack getStackInSlot(int slot) {
            return stack();
        }

        @Override
        public int getSize() {
            return 1;
        }

        public static Input of(ItemStack stack){
            return new Input(stack);
        }
    }

    public int getDegrees(){
        return this.degrees;
    }

    @Override
    public boolean matches(Input input, World world) {
        return this.input.test(input.stack());
    }

    @Override
    public ItemStack craft(Input input, RegistryWrapper.WrapperLookup wrapperLookup) {
        return this.getActualOutput(input);
    }

    @Override
    public boolean fits(int width, int height) {
        return (width & height) > 0;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output().copy();
    }

    public ItemStack getActualOutput(Input input){
        if (this.copyNbt){
            NbtCompound nbt = ModItems.getNbt(input.getStackInSlot(0));
            if (nbt != null) {
                ItemStack output = this.output().copy();
                ModItems.setNbt(output, nbt.copy());
                return output;
            }
        }
        return this.output().copy();
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return MillstoneRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public static class Type implements RecipeType<MillstoneRecipe> {
        public static final Identifier ID = Identifier.of(Synchro.MOD_ID, "millstone_type");
        private Type() {}
    }

}
