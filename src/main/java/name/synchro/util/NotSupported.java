package name.synchro.util;

import com.google.common.collect.ImmutableSet;
import name.synchro.registrations.BlocksRegistered;
import name.synchro.registrations.ItemsRegistered;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.Set;

public final class NotSupported {
    private static final Set<Item> entries = ImmutableSet.of(
            ItemsRegistered.RAW_MIXED_ORE,
            BlocksRegistered.MIXED_ORE.asItem(),
            BlocksRegistered.AOITE.asItem(),
            BlocksRegistered.SEKITE.asItem(),
            BlocksRegistered.BAAKITE.asItem(),
            BlocksRegistered.HAAKITE.asItem(),
            BlocksRegistered.GUMITE.asItem(),
            BlocksRegistered.NGANITE.asItem(),
            BlocksRegistered.MURAXKITE.asItem(),
            BlocksRegistered.MIDORITE.asItem()
            );
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (entries.contains(stack.getItem())) {
                lines.add(Text.translatable("tooltip.synchro.not_supported"));
            }
        });
    }
}
