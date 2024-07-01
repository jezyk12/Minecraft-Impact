package name.synchro.specialRecipes;

import name.synchro.Synchro;
import name.synchro.blockEntities.MillstoneBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MillstoneRecipe implements Recipe<Inventory> {
    public static final RecipeType<MillstoneRecipe> MILLING_RECIPE_TYPE = new Type();
    protected final Identifier id;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int degrees;
    protected final boolean copyNbt;
    public MillstoneRecipe(Identifier id, Ingredient input, ItemStack output, int degrees, boolean copyNbt) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.degrees = degrees;
        this.copyNbt = copyNbt;
    }

    public Ingredient getInput(){
        return this.input;
    }

    public int getDegrees(){
        return this.degrees;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (inventory.size() != MillstoneBlockEntity.INV_SIZE) return false;
        return this.input.test(inventory.getStack(MillstoneBlockEntity.SLOT_INPUT));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return (width & height) > 0;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    public ItemStack getOutput(DynamicRegistryManager registryManager, ItemStack inputStack){
        if (this.copyNbt && inputStack.hasNbt()){
            NbtCompound nbt = inputStack.getNbt().copy();
            ItemStack output = this.output.copy();
            output.setNbt(nbt);
            return output;
        }
        return getOutput(registryManager);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MillstoneRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return MILLING_RECIPE_TYPE;
    }

    public static class Type implements RecipeType<MillstoneRecipe> {
        public static final Identifier ID = new Identifier(Synchro.MOD_ID, "millstone_type");
        private Type() {}
    }
}
