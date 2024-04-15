package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.synchroBlocks.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

import static name.synchro.registrations.RegisterItemGroups.SYNCHRO_BASIC;

public class RegisterBlocks {
    public static final MixedOre MIXED_ORE = registerBlock("mixed_ore",
            new MixedOre(FabricBlockSettings.of(Material.STONE).strength(0.1f)),SYNCHRO_BASIC);
    public static final CableBlock CABLE = registerBlock("cable",
            new CableBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f).nonOpaque()),SYNCHRO_BASIC);
    public static final ElectricSourceBlock ELECTRIC_SOURCE = registerBlock("electric_source",
            new ElectricSourceBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);
    public static final ElectricLampBlock ELECTRIC_LAMP = registerBlock("electric_lamp",
            new ElectricLampBlock(FabricBlockSettings.of(Material.METAL)
                    .luminance(createLightLevelFromLitBlockState(15)).strength(0.1f)),SYNCHRO_BASIC);
    public static final DebugBlock DEBUG_BLOCK = registerBlock("debug_block",
            new DebugBlock(FabricBlockSettings.of(Material.METAL).strength(0.1f)),SYNCHRO_BASIC);

    public static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
    protected static <T extends Block> T registerBlock(String path, T block, ItemGroup itemGroup){
        RegisterItems.registerItem(path, new BlockItem(block,new FabricItemSettings()), itemGroup);
        return Registry.register(Registries.BLOCK, new Identifier(Synchro.MOD_ID, path), block);
    }
    public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod blocks for" + Synchro.MOD_ID);
    }
}
