package name.synchro.modUtilData.locationAction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public record DropItems(List<ItemStack> stacks) implements LocationAction {
    public static final MapCodec<DropItems> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(ItemStack.CODEC).fieldOf("items").forGetter(DropItems::stacks)
    ).apply(instance, DropItems::new));


    @Override
    public Type<?> getType() {
        return DROP_ITEMS;
    }

    @Override
    public void act(World world, BlockPos blockPos) {
        for (ItemStack stack : stacks()) {
            ItemScatterer.spawn(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, stack);
        }
    }
}
