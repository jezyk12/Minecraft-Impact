package name.synchro.items;

import name.synchro.api.ContextualItemTooltipData;
import name.synchro.api.ItemSpeciallyCombinable;
import name.synchro.mixinHelper.MetalsProvider;
import name.synchro.registrations.ItemsRegistered;
import name.synchro.util.Metals;
import name.synchro.util.MetalsComponentsHelper;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
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
            if (item == ItemsRegistered.LUMP_ORES) return LUMP;
            else if (item == ItemsRegistered.CRACKED_ORES) return CRACKED;
            else if (item == ItemsRegistered.CRUSHED_ORES) return CRUSHED;
            else if (item == ItemsRegistered.ORES_DUST) return DUST;
            else return NONE;
        }
    }

    private record CrackedOreTooltipData(Map<Integer, Integer> contents, boolean advanced, boolean creative, int drpTotal, MinecraftClient client) implements TooltipData {}

    private record CrackedOreTooltipComponent(List<Pair<Integer, Integer>> sortedContents, boolean advanced, boolean creative, int rockContent, MinecraftClient client) implements TooltipComponent {
        @Override
        public int getHeight() {
            if (creative()){
                return (sortedContents().size() + 2) * 9;
            }
            return 0;
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            if (creative()){
                String nameMetal = Text.translatable("tooltip.synchro.metal").getString();
                return textRenderer.getWidth(Text.of("■ " + nameMetal + "00 00000drp"));
            }
            return 0;
        }

        @Override
        public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
            TooltipComponent.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
            if (creative() && client.world != null) {
                textRenderer.draw(Text.translatable("tooltip.synchro.metal_content"), x, y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
                Metals metals = ((MetalsProvider) client.world).getMetals();
                String nameMetal = Text.translatable("tooltip.synchro.metal").getString();
                for (int i = 0; i < sortedContents().size(); i++) {
                    Integer numId = sortedContents().get(i).getLeft();
                    Integer content = sortedContents().get(i).getRight();
                    Text line = Text.of("■ " + nameMetal + (numId > 9 ? numId : "0" + numId) + " " + content + "drp");
                    int color = metals.getVariants().get(numId).color();
                    textRenderer.draw(line, x, y + 9 * (i + 1), color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
                }
                Text lineOfRock = Text.of("■ " + Text.translatable("tooltip.synchro.rock").getString() + " " + rockContent() + "drp");
                textRenderer.draw(lineOfRock, x, y + 9 * (sortedContents().size() + 1), 0xd8d8d8, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            }
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer) {
            TooltipComponent.super.drawItems(textRenderer, x, y, matrices, itemRenderer);
        }
    }

    public static void registerTooltipEvent(){
        TooltipComponentCallback.EVENT.register(tooltipData -> {
            if (tooltipData instanceof CrackedOreTooltipData crackedOreTooltipData){
                Map<Integer, Integer> contents = crackedOreTooltipData.contents();
                List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                contents.forEach((k, v) -> pairs.add(new Pair<>(k, v)));
                pairs.sort((a, b) -> b.getRight() - a.getRight());
                return new CrackedOreTooltipComponent(pairs, crackedOreTooltipData.advanced(), crackedOreTooltipData.creative(),
                        MetalsComponentsHelper.getRockContent(contents, crackedOreTooltipData.drpTotal()), crackedOreTooltipData.client());
            }
            return TooltipComponent.of(OrderedText.EMPTY);
        });
    }
}
