package name.synchro.specialRecipes;

import name.synchro.Synchro;
import name.synchro.registrations.RegisterItems;
import name.synchro.util.NbtTags;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MillstoneRecipes {
    private static final HashMap<Item, Entry> RECIPES = new HashMap<>();

    private record Entry(Predicate<ItemStack> canInput, Function<ItemStack, ItemStack> output, int processDegree) {
    }

    public static boolean canInput(ItemStack input) {
        return RECIPES.containsKey(input.getItem());
    }

    public static boolean canProcess(ItemStack input) {
        if (RECIPES.containsKey(input.getItem())) {
            return RECIPES.get(input.getItem()).canInput().test(input);
        }
        return false;
    }

    public static ItemStack productOf(ItemStack input) {
        if (canProcess(input)) {
            return RECIPES.get(input.getItem()).output().apply(input);
        }
        return ItemStack.EMPTY;
    }

    public static int processDegreeOf(ItemStack input) {
        if (canProcess(input)) {
            return RECIPES.get(input.getItem()).processDegree();
        }
        return -1;
    }

    public static class Builder {
        @Nullable
        protected ItemConvertible item;
        private ItemConvertible[] items;
        protected Predicate<ItemStack> conditions;
        private Function<ItemStack, ItemStack> output;
        private int processDegree = 90;

        protected Builder() {}

        protected static Builder of(ItemConvertible item) {
            Builder builder = new Builder();
            builder.item = item;
            builder.conditions = itemStack -> true;
            return builder;
        }

        private static Builder of(ItemConvertible... items) {
            Builder builder = new Builder();
            builder.items = items;
            builder.conditions = itemStack -> true;
            return builder;
        }

        public Builder conditions(Predicate<ItemStack> canInput) {
            this.conditions = canInput;
            return this;
        }

        public Builder output(Function<ItemStack, ItemStack> output) {
            this.output = output;
            return this;
        }

        public Builder output(ItemConvertible output) {
            this.output = stack -> new ItemStack(output);
            return this;
        }

        public Builder output(ItemConvertible output, int count) {
            this.output = stack -> new ItemStack(output, count);
            return this;
        }

        public Builder spends(int processDegree) {
            this.processDegree = processDegree;
            return this;
        }

        public void build() {
            if (item != null) {
                RECIPES.put(item.asItem(), new Entry(conditions, output, processDegree));
            }
            else if (items != null) {
                for (ItemConvertible item : items) {
                    RECIPES.put(item.asItem(), new Entry(conditions, output, processDegree));
                }
            }
            else {
                Synchro.LOGGER.error("Failed to build Millstone recipe: No item to build");
            }
        }
    }

    public static void buildAll(){
        Builder.of(Blocks.OAK_PLANKS, Blocks.ACACIA_PLANKS, Blocks.BIRCH_PLANKS, Blocks.DARK_OAK_PLANKS,
                Blocks.BAMBOO_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.MANGROVE_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.CHERRY_PLANKS)
                .spends(360).output(RegisterItems.PLANT_FIBRE, 4).build();
        Builder.of(Blocks.OAK_LOG, Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
                        Blocks.JUNGLE_LOG, Blocks.MANGROVE_LOG, Blocks.SPRUCE_LOG, Blocks.CHERRY_LOG)
                .spends(1620).output(RegisterItems.PLANT_FIBRE, 16).build();
        Builder.of(Items.STICK, Items.BAMBOO).spends(90).output(RegisterItems.PLANT_FIBRE, 2).build();
        Builder.of(Items.GLASS, Items.FERN).spends(45).output(RegisterItems.PLANT_FIBRE).build();
        Builder.of(RegisterItems.LUMP_ORES, RegisterItems.CRACKED_ORES, RegisterItems.CRUSHED_ORES)
                .conditions(stack -> stack.hasNbt() && stack.getNbt().contains(NbtTags.CONTENT))
                .output(input -> {
                    ItemStack output = new ItemStack(RegisterItems.ORES_DUST);
                    output.setNbt(input.getNbt().copy());
                    return output;
                }).spends(360).build();

        Synchro.LOGGER.debug("Millstone recipes built successfully.");
    }
}
