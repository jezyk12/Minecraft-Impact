package name.synchro.util;

import com.google.common.collect.ImmutableSet;
import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.Set;

public final class NotSupported {
    private static final Set<Item> entries = ImmutableSet.of(
            ModItems.RAW_MIXED_ORE,
            ModBlocks.DIRT_CANAL.asItem(),
            ModBlocks.AOITE.asItem(),
            ModBlocks.SEKITE.asItem(),
            ModBlocks.BAAKITE.asItem(),
            ModBlocks.HAAKITE.asItem(),
            ModBlocks.GUMITE.asItem(),
            ModBlocks.NGANITE.asItem(),
            ModBlocks.MURAXKITE.asItem(),
            ModBlocks.MIDORITE.asItem()
            );
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (entries.contains(stack.getItem())) {
                lines.add(Text.translatable("tooltip.synchro.not_supported"));
            }
        });
    }
}
