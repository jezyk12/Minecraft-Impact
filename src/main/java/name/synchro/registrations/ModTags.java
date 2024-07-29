package name.synchro.registrations;

import name.synchro.Synchro;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModTags {
    //Block Tags
    public static TagKey<Block> CAN_STORE_FLUID =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "can_store_fluid"));
    public static TagKey<Block> NEVER_FILL_FLUID =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "never_fill_fluid"));
    public static TagKey<Block> NEVER_WATER_COEXIST =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "never_water_coexist"));
    public static TagKey<Block> NEVER_LAVA_COEXIST =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "never_lava_coexist"));
    public static TagKey<Block> WASH_AWAY_BY_WATER =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "wash_away_by_water"));
    public static TagKey<Block> WASH_AWAY_BY_LAVA =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(Synchro.MOD_ID, "wash_away_by_lava"));

    //Item Tags
    public static TagKey<Item> RAW_METAL_ORE =
            TagKey.of(RegistryKeys.ITEM, Identifier.of(Synchro.MOD_ID, "raw_metal_ore"));

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered tags for" + Synchro.MOD_ID);
    }
}
