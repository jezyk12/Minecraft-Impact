package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class RegisterItemGroups {
    public static ItemGroup SYNCHRO_BASIC =
            FabricItemGroup.builder(new Identifier(Synchro.MOD_ID,"synchro_basic"))
                    .displayName(Text.translatable("itemgroup.synchro.synchro_basic"))
                    .icon(() -> new ItemStack(BlocksRegistered.MIXED_ORE)).build();
    public static ItemGroup SYNCHRO_DECORATION =
            FabricItemGroup.builder(new Identifier(Synchro.MOD_ID,"synchro_decoration"))
                    .displayName(Text.translatable("itemgroup.synchro.synchro_decoration"))
                    .icon(() -> new ItemStack(BlocksRegistered.OAK_PLANKS_SLOPE)).build();
    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod item groups for "+Synchro.MOD_ID);
    }
}
