package name.synchro.api;

import name.synchro.Synchro;
import name.synchro.specialRecipes.MillstoneRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MillstoneRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
    private final Ingredient input;
    private final ItemStack output;
    private final int degrees;
    private boolean copyNbt;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable private String group;
    @Nullable private RecipeCategory category;

    protected MillstoneRecipeJsonBuilder(Ingredient input, ItemStack output, int degrees) {
        this.input = input;
        this.output = output;
        this.degrees = degrees;
    }

    public static MillstoneRecipeJsonBuilder create(Ingredient input, ItemStack output, int degrees) {
        return new MillstoneRecipeJsonBuilder(input, output, degrees);
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public CraftingRecipeJsonBuilder category(RecipeCategory category){
        this.category = category;
        return this;
    }

    public CraftingRecipeJsonBuilder copyNbt(){
        this.copyNbt = true;
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, String recipePath) {
        offerTo(exporter, Identifier.of(Synchro.MOD_ID, "millstone/" + recipePath));
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        MillstoneRecipe millstoneRecipe = validate(recipeId);
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        String folderStr = "";
        if (this.category != null) {
             folderStr = this.category.getName() + "/";
        }
        exporter.accept(recipeId, millstoneRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + folderStr)));
    }

    private MillstoneRecipe validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        } else {
            return new MillstoneRecipe(input, output, degrees, copyNbt);
        }
    }

//    protected static class JsonProvider implements RecipeJsonProvider{
//        private final Identifier recipeId;
//        @Nullable private final String group;
//        @Nullable private final RecipeCategory category;
//        private final Ingredient input;
//        private final ItemStack output;
//        private final int degrees;
//        private final boolean copyNbt;
//        @Nullable private final Advancement.Builder advancementBuilder;
//        @Nullable private final Identifier advancementId;
//        private final RecipeSerializer<MillstoneRecipe> serializer;
//
//        protected JsonProvider(Identifier recipeId, @Nullable String group, @Nullable RecipeCategory category, Ingredient input, ItemStack output, int degrees, boolean copyNbt, Advancement.@Nullable Builder advancementBuilder, @Nullable Identifier advancementId, RecipeSerializer<MillstoneRecipe> serializer) {
//            this.recipeId = recipeId;
//            this.group = group;
//            this.category = category;
//            this.input = input;
//            this.output = output;
//            this.degrees = degrees;
//            this.copyNbt = copyNbt;
//            this.advancementBuilder = advancementBuilder;
//            this.advancementId = advancementId;
//            this.serializer = serializer;
//        }
//
//        @Override
//        public void serialize(JsonObject json) {
//            if (this.group != null) {
//                json.addProperty("group", this.group);
//            }
//            if (this.category != null){
//                json.addProperty("category", this.category.getName());
//            }
//            json.add("input", this.input.toJson());
//            json.addProperty("output", Registries.ITEM.getId(this.output.getItem()).toString());
//            json.addProperty("amount", this.output.getCount());
//            json.addProperty("degrees", this.degrees);
//            json.addProperty("copyNbt", this.copyNbt);
//        }
//
//        @Override
//        public Identifier getRecipeId() {
//            return this.recipeId;
//        }
//
//        @Override
//        public RecipeSerializer<?> getSerializer() {
//            return this.serializer;
//        }
//
//        @Nullable
//        @Override
//        public JsonObject toAdvancementJson() {
//            if (this.advancementBuilder != null){
//                return this.advancementBuilder.toJson();
//            }
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public Identifier getAdvancementId() {
//            return this.advancementId;
//        }
//    }
}
