package name.synchro.items;

import name.synchro.api.ItemSpeciallyCombinable;
import name.synchro.registrations.ModItems;
import name.synchro.util.Metals;
import name.synchro.util.MetalsComponentsHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OresMixture extends Item implements ItemSpeciallyCombinable {
    public OresMixture(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (slot.canInsert(cursorStack) && cursorStack.getItem() instanceof ItemSpeciallyCombinable) {
            ItemStack slotStack = slot.getStack();
            if (slotStack.isEmpty()){
                if (clickType.equals(ClickType.LEFT)){
                    slot.setStack(cursorStack.copy());
                    slot.markDirty();
                    cursorStack.setCount(0);
                    return true;
                }
                else if (clickType.equals(ClickType.RIGHT)){
                    ItemStack unitStack = cursorStack.split(1);
                    slot.setStack(unitStack);
                    slot.markDirty();
                    return true;
                }
            }
        }
        return super.onStackClicked(cursorStack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (cursorStack.getItem() instanceof ItemSpeciallyCombinable specialItem) {
            if (specialItem.canCombineToExistingStack(cursorStack, stack)) {
                if (clickType.equals(ClickType.LEFT)){
                    if (slot.canTakeItems(player)){
                        cursorStackReference.set(specialItem.combineToExistingStack(cursorStack, stack));
                        return true;
                    }
                }
                else if (clickType.equals(ClickType.RIGHT)){
                    if (slot.canInsert(cursorStack)){
                        ItemStack unitStack = cursorStack.copy();
                        unitStack.setCount(1);
                        specialItem.combineToExistingStack(unitStack, stack);
                        if (unitStack.getCount() < 1) {
                            cursorStack.decrement(1);
                            cursorStackReference.set(cursorStack);
                            return true;
                        }
                    }
                }
            }
        }
        return super.onClicked(stack, cursorStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        NbtCompound nbtCompound = ModItems.getNbt(stack);
        if (nbtCompound != null){
            Map<Integer, Integer> contents = Metals.getMetalContentFromNbt(nbtCompound);
            return Optional.of(new TooltipDataRecord(contents, switch (Type.of(stack.getItem())){
                case LUMP -> MetalsComponentsHelper.DRP_LUMP_ORES;
                case CRACKED -> MetalsComponentsHelper.DRP_CRACKED_ORES;
                case CRUSHED -> MetalsComponentsHelper.DRP_CRUSHED_ORES;
                case DUST -> MetalsComponentsHelper.DRP_ORES_DUST;
                default -> MetalsComponentsHelper.DRP_BLOCK;
            }));
        }
        return Optional.empty();
    }

    @Override
    public boolean canCombineToExistingStack(ItemStack givenStack, ItemStack existingStack, boolean allowOverflow) {
        return givenStack.isOf(existingStack.getItem())
                && ModItems.hasNbt(givenStack)
                && ModItems.hasNbt(existingStack)
                && (allowOverflow || existingStack.getCount() < this.getMaxCount());
    }

    @Override
    public ItemStack combineToExistingStack(ItemStack givenStack, ItemStack existingStack) {
        NbtCompound nbt1 = ModItems.getNbt(givenStack);
        NbtCompound nbt2 = ModItems.getNbt(existingStack);
        if (nbt1 != null && nbt2 != null){
            int transferCount;
            if (existingStack.getCount() + givenStack.getCount() > this.getMaxCount()) {
                transferCount = this.getMaxCount() - existingStack.getCount();
            }
            else transferCount = givenStack.getCount();
            Metals.combineMetalsNbt(nbt2, existingStack.getCount(), nbt1, transferCount);
            ModItems.setNbt(existingStack, nbt2);
            existingStack.setCount(transferCount + existingStack.getCount());
            givenStack.setCount(givenStack.getCount() - transferCount);
        }
        return givenStack;
    }

    public enum Type {
        LUMP,
        CRACKED,
        CRUSHED,
        DUST,
        NONE;
        public static Type of(Item item){
            if (item == ModItems.LUMP_ORES) return LUMP;
            else if (item == ModItems.CRACKED_ORES) return CRACKED;
            else if (item == ModItems.CRUSHED_ORES) return CRUSHED;
            else if (item == ModItems.ORES_DUST) return DUST;
            else return NONE;
        }
    }

    public record TooltipDataRecord(Map<Integer, Integer> contents, int drpTotal) implements TooltipData {}

}
