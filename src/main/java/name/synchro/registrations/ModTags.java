package name.synchro.registrations;

import name.synchro.Synchro;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModTags {
    //Block Tags
    public static TagKey<Block> CAN_STORE_FLUID =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("can_store_fluid"));
    public static TagKey<Block> NEVER_FILL_FLUID =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("never_fill_fluid"));
    public static TagKey<Block> DESTROY_IN_WATER =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("destroy_in_water"));
    public static TagKey<Block> BURN_AWAY_IN_LAVA =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("burn_away_in_lava"));
    public static TagKey<Block> WASH_AWAY_BY_WATER =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("wash_away_by_water"));
    public static TagKey<Block> WASH_AWAY_BY_LAVA =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("wash_away_by_lava"));
    public static TagKey<Block> BURNABLE_FENCE =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("burnable_fence"));
    public static TagKey<Block> BURNABLE_SLAB =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("burnable_slab"));
    public static TagKey<Block> BURNABLE_STAIRS =
            TagKey.of(RegistryKeys.BLOCK, Synchro.id("burnable_stairs"));
    //Item Tags
    public static TagKey<Item> RAW_METAL_ORE =
            TagKey.of(RegistryKeys.ITEM, Synchro.id( "raw_metal_ore"));

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered tags for" + Synchro.MOD_ID);
    }
}
