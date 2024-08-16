package name.synchro.dataGeneration;

import name.synchro.api.CowWorkingFeedDataProvider;
import name.synchro.registrations.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class DefaultCowWorkingFeedsData extends CowWorkingFeedDataProvider {
    public DefaultCowWorkingFeedsData(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate(RegistryWrapper.WrapperLookup wrapperLookup) {
        addEntry(Ingredient.ofItems(ModItems.FRESH_FORAGE, Items.GOLDEN_APPLE), 3600);
        addEntry(Ingredient.ofItems(Items.CAKE, Blocks.HAY_BLOCK, Items.ENCHANTED_GOLDEN_APPLE), 7600);
        addEntry(Ingredient.fromTag(ItemTags.COW_FOOD), 800);
        addEntry(Ingredient.ofItems(Items.SHORT_GRASS, Items.FERN), 300);
        addEntry(Ingredient.ofItems(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM), 300);
    }
}
