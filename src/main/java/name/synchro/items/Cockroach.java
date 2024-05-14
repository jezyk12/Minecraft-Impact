package name.synchro.items;

import name.synchro.registrations.RegisterItems;
import name.synchro.util.NbtTags;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
            slot.setStack(driveAwayCockroach(stack));
            cursorStackReference.set(new ItemStack(RegisterItems.COCKROACH,count));
            return true;
        }
        else return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()){
            assert stack.getNbt() != null;
            if (stack.getNbt().get(NbtTags.COCKROACH_HOLDS) instanceof NbtCompound holdsNbt) {
                ItemStack holds = ItemStack.fromNbt(holdsNbt);
                int holdsCount = holds.getCount();
                if (holdsCount > 0) {
                    tooltip.add(Text.translatable("info.synchro.biting")
                            .append(holds.getItem().getName().copy())
                            .append(Text.of(" × " + holdsCount)));
                }
            }
        }
    }

    protected static ItemStack driveAwayCockroach(ItemStack cockroachStack){
        if (cockroachStack.hasNbt()){
            assert cockroachStack.getNbt() != null;
            if (cockroachStack.getNbt().get(NbtTags.COCKROACH_HOLDS) instanceof NbtCompound holdsNbt){
                ItemStack holds = ItemStack.fromNbt(holdsNbt);
                Random random = new Random();
                int stackCount = cockroachStack.getCount();
                holds.decrement((random.nextInt(stackCount) + random.nextInt(stackCount)) / 2);
                return holds;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack hold(int cockroachAmount, ItemStack stack){
        NbtCompound compound = new NbtCompound();
        NbtCompound cockroachHolds = stack.writeNbt(new NbtCompound());
        compound.put(NbtTags.COCKROACH_HOLDS,cockroachHolds);
        ItemStack cockroachStack = new ItemStack(RegisterItems.COCKROACH, cockroachAmount);
        cockroachStack.setNbt(compound);
        return cockroachStack;
    }

    public static boolean loves(ItemStack stack){
        return stack.getItem().isFood();
    }

    public static <T extends Inventory> void summon(T container, float possibilityMultiplier) {
        Cockroach.summon(container, possibilityMultiplier, 0);
    }

    private static <T extends Inventory> boolean summon(T container, float possibilityMultiplier, int times){
        if (possibilityMultiplier > 1f) possibilityMultiplier = 1f;
        Random random = new Random();
        int chosen = random.nextInt((int) (container.size() * 70 / possibilityMultiplier));
        int cockroachCount = 0;
        for (int i = 0; i < container.size(); ++i){
            ItemStack stack = container.getStack(i);
            int possibility;
            boolean cockroachExisted = false;
            int stackCount = stack.getCount();
            if (stack.isOf(RegisterItems.COCKROACH)) cockroachCount += stackCount;
            if (Cockroach.loves(stack)){
                possibility = 24 + 4 * stackCount * 64 / stack.getItem().getMaxCount();
            }
            else if (stack.isOf(RegisterItems.COCKROACH)){
                possibility = 16 - stackCount;
                cockroachExisted = true;
            }
            else {
                possibility = 6 + stackCount * 64 / stack.getItem().getMaxCount();
            }
            chosen -= possibility;
            if (chosen < 0){
                if (!cockroachExisted){
                    container.setStack(i, Cockroach.hold(1, stack));
                }
                else {
                    container.getStack(i).increment(1);
                }
                return true;
            }
        }
        if (times < 5 && random.nextInt(container.size()) < cockroachCount){
            return Cockroach.summon(container, possibilityMultiplier, times + 1);
        }
        return false;
    }
}
