package name.synchro.items;

import name.synchro.api.ContextualItemTooltipData;
import name.synchro.api.ItemSpeciallyCombinable;
import name.synchro.registrations.ModItems;
import name.synchro.util.Metals;
import name.synchro.util.MetalsComponentsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.Map;
import java.util.Optional;

public class OresMixture extends Item implements ContextualItemTooltipData, ItemSpeciallyCombinable {
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Optional<TooltipData> getTooltipDataConditionally(ItemStack stack, TooltipContext context, MinecraftClient client) {
        if (stack.hasNbt()){
            Map<Integer, Integer> contents = Metals.getMetalContentFromNbt(stack.getNbt());
            return Optional.of(new CrackedOreTooltipData(contents, context.isAdvanced(), context.isCreative(), switch (Type.of(stack.getItem())){
                case LUMP -> MetalsComponentsHelper.DRP_LUMP_ORES;
                case CRACKED -> MetalsComponentsHelper.DRP_CRACKED_ORES;
                case CRUSHED -> MetalsComponentsHelper.DRP_CRUSHED_ORES;
                case DUST -> MetalsComponentsHelper.DRP_ORES_DUST;
                default -> MetalsComponentsHelper.DRP_BLOCK;
            }, client));
        }
        return Optional.empty();
    }

    @Override
    public boolean canCombineToExistingStack(ItemStack givenStack, ItemStack existingStack) {
        return givenStack.isOf(existingStack.getItem()) && givenStack.hasNbt() && existingStack.hasNbt() && existingStack.getCount() < this.getMaxCount();
    }

    @Override
    public ItemStack combineToExistingStack(ItemStack givenStack, ItemStack existingStack) {
        if (givenStack.hasNbt() && existingStack.hasNbt()){
            NbtCompound nbt1 = givenStack.getNbt();
            NbtCompound nbt2 = existingStack.getNbt();
            int transferCount;
            if (existingStack.getCount() + givenStack.getCount() > this.getMaxCount()) {
                transferCount = this.getMaxCount() - existingStack.getCount();
            }
            else transferCount = givenStack.getCount();
            Metals.combineMetalsNbt(nbt2, existingStack.getCount(), nbt1, transferCount);
            existingStack.setNbt(nbt2);
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

    public record CrackedOreTooltipData(Map<Integer, Integer> contents, boolean advanced, boolean creative, int drpTotal, MinecraftClient client) implements TooltipData {}

}
