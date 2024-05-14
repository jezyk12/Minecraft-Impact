package name.synchro.api;

import name.synchro.items.OresMixture;
import net.minecraft.item.ItemStack;

/**
 *  @apiNote Item which implements this interface will be suitable for destroying rock blocks.
 */
public interface SuitableForRock {
    /**
     * @return Whether rock block will drop itself instead of cracked ores, like silk touch.
     */
    boolean dropWholeBlock(ItemStack stack);

    /**
     * @return Whether rock block will drop itself with  NBT data of metal content.
     * Only works when {@link #dropWholeBlock(ItemStack)} returns true.
     */
    boolean keepNbt(ItemStack stack);

    /**
     * @return Decides how the ores drop.
     */
    OresMixture.Level oresLevel(ItemStack stack);
}
