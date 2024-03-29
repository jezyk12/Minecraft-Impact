package name.synchro.specialRecipes;

import name.synchro.synchroItems.RawMixedOre;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;

import static name.synchro.synchroItems.RawMixedOre.AVAILABLE_ORE_LIST;
import static name.synchro.registrations.RegisterItems.RAW_MIXED_ORE;
import static name.synchro.registrations.RegisterRecipes.MIX_ORE_RECIPE;

public class MixOreRecipe extends SpecialCraftingRecipe {
    public MixOreRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }
    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean result=false;
        for(int i=0;i<=inventory.size();++i) {
            if(!inventory.getStack(i).isEmpty()) {
                if (!(inventory.getStack(i).getItem() instanceof RawMixedOre)) return false;
                else if(!inventory.getStack(i).hasNbt()) return false;
                else {
                    for (String ore : AVAILABLE_ORE_LIST){
                        if (inventory.getStack(i).getNbt().get(ore) == null) return false;
                    }
                    result=true;
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        NbtCompound resultNbt = new NbtCompound();
        HashMap<String,Integer> nbtMap = new HashMap<>();
        int totalCount = 0;
        for(int i=0;i<=inventory.size();++i) {
            if (inventory.getStack(i).hasNbt()) {
                totalCount += 1;
                for (String ore : AVAILABLE_ORE_LIST) {
                    if (nbtMap.get(ore) == null) nbtMap.put(ore, inventory.getStack(i).getNbt().getInt(ore));
                    else nbtMap.put(ore, nbtMap.get(ore) + inventory.getStack(i).getNbt().getInt(ore));
                }
            }

        }
        ItemStack result = new ItemStack(RAW_MIXED_ORE,totalCount);
        for(String ore:AVAILABLE_ORE_LIST){
            resultNbt.putInt(ore,nbtMap.get(ore)/totalCount);
        }
        result.setNbt(resultNbt);
        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width*height!=0;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MIX_ORE_RECIPE;
    }
}
