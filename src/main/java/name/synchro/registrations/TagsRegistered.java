package name.synchro.registrations;

import name.synchro.Synchro;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class TagsRegistered {
    public static TagKey<Block> CAN_FILL_FLUID =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "can_fill_fluid"));
    public static TagKey<Block> NEVER_FILL_FLUID =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "never_fill_fluid"));
    public static TagKey<Block> NEVER_FLOW_FLUID =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "never_flow_fluid"));
    public static TagKey<Block> NEVER_WATER_COEXIST =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "never_water_coexist"));
    public static TagKey<Block> NEVER_LAVA_COEXIST =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "never_lava_coexist"));
    public static TagKey<Block> WASH_AWAY_BY_WATER =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "wash_away_by_water"));
    public static TagKey<Block> WASH_AWAY_BY_LAVA =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Synchro.MOD_ID, "wash_away_by_lava"));
    public static void registerAll(){
        Synchro.LOGGER.debug("Registered tags for" + Synchro.MOD_ID);
    }
}
