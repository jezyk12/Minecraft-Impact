package name.synchro.items;

import name.synchro.registrations.ModItems;
import name.synchro.util.NbtTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;

import java.util.List;
import java.util.Random;

public class Cockroach extends Item {

    public Cockroach(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (otherStack.isEmpty()){
            int count = stack.getCount();
            slot.setStack(driveAwayCockroach(stack, player.getWorld().getRegistryManager(), player.getWorld().getTime()));
            cursorStackReference.set(new ItemStack(ModItems.COCKROACH,count));
            return true;
        }
        else return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        NbtCompound nbtCompound = ModItems.getNbt(stack);
        if (nbtCompound == null) return;
        if (nbtCompound.get(NbtTags.COCKROACH_HOLDS) instanceof NbtCompound holdsNbt && context.getRegistryLookup() != null) {
            ItemStack holds = ModItems.decode(holdsNbt, context.getRegistryLookup());
            if (!holds.isEmpty()){
                int holdsCount = holds.getCount();
                if (holdsCount > 0) {
                    tooltip.add(Text.translatable("info.synchro.biting")
                            .append(holds.getItem().getName().copy())
                            .append(Text.of(" × " + holdsCount)));
                }
            }
        }
    }

    protected static ItemStack driveAwayCockroach(ItemStack cockroachStack, RegistryWrapper.WrapperLookup wrapperLookup, long seed){
        NbtCompound nbtCompound = ModItems.getNbt(cockroachStack);
        if (nbtCompound == null) return ItemStack.EMPTY;
        if (nbtCompound.get(NbtTags.COCKROACH_HOLDS) instanceof NbtCompound holdsNbt){
            ItemStack holds = ModItems.decode(holdsNbt, wrapperLookup);
            if (!holds.isEmpty()){
                Random random = new Random(seed);
                int stackCount = cockroachStack.getCount();
                holds.decrement((random.nextInt(stackCount) + random.nextInt(stackCount)) / 2);
                return holds;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack hold(int cockroachAmount, ItemStack stack, RegistryWrapper.WrapperLookup wrapperLookup){
        NbtCompound compound = new NbtCompound();
        NbtElement cockroachHolds = ModItems.encode(stack, wrapperLookup);
        compound.put(NbtTags.COCKROACH_HOLDS,cockroachHolds);
        ItemStack cockroachStack = new ItemStack(ModItems.COCKROACH, cockroachAmount);
        ModItems.setNbt(cockroachStack, compound);
        return cockroachStack;
    }

    public static boolean loves(ItemStack stack){
        return stack.getComponents().get(DataComponentTypes.FOOD) != null;
    }

    public static <T extends Inventory> void summon(T container, float possibilityMultiplier, RegistryWrapper.WrapperLookup wrapperLookup) {
        Cockroach.summon(container, possibilityMultiplier, 0, wrapperLookup);
    }

    private static <T extends Inventory> boolean summon(T container, float possibilityMultiplier, int times, RegistryWrapper.WrapperLookup wrapperLookup){
        if (possibilityMultiplier > 1f) possibilityMultiplier = 1f;
        Random random = new Random();
        int chosen = random.nextInt((int) (container.size() * 70 / possibilityMultiplier));
        int cockroachCount = 0;
        for (int i = 0; i < container.size(); ++i){
            ItemStack stack = container.getStack(i);
            int possibility;
            boolean cockroachExisted = false;
            int stackCount = stack.getCount();
            if (stack.isOf(ModItems.COCKROACH)) cockroachCount += stackCount;
            if (Cockroach.loves(stack)){
                possibility = 24 + 4 * stackCount * 64 / stack.getItem().getMaxCount();
            }
            else if (stack.isOf(ModItems.COCKROACH)){
                possibility = 16 - stackCount;
                cockroachExisted = true;
            }
            else {
                possibility = 6 + stackCount * 64 / stack.getItem().getMaxCount();
            }
            chosen -= possibility;
            if (chosen < 0){
                if (!cockroachExisted){
                    container.setStack(i, Cockroach.hold(1, stack, wrapperLookup));
                }
                else {
                    container.getStack(i).increment(1);
                }
                return true;
            }
        }
        if (times < 5 && random.nextInt(container.size()) < cockroachCount){
            return Cockroach.summon(container, possibilityMultiplier, times + 1, wrapperLookup);
        }
        return false;
    }
}
