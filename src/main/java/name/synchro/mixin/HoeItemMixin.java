package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import name.synchro.registrations.RegisterBlocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(HoeItem.class)
public class HoeItemMixin {
	@ModifyVariable
            (method = "useOnBlock",
            at = @At("STORE"),
            ordinal = 0)
	private Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> useOnBlockMixin
            (Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair,
             @Local World world,
             @Local BlockPos blockPos) {
        if (world.getBlockState(blockPos).isOf(RegisterBlocks.FERTILE_DIRT)) {
            pair = Pair.of(HoeItem::canTillFarmland, HoeItem.createTillAction(RegisterBlocks.FERTILE_FARMLAND.getDefaultState()));
        }
		return pair;
    }
}