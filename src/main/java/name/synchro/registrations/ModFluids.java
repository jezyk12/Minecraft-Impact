package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.fluids.gases.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModFluids {
    public static final WaterVaporGas WATER_VAPOR_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "water_vapor_gas"), new WaterVaporGas());
    public static final HotSteamGas HOT_STEAM_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "hot_steam_gas"), new HotSteamGas());
    public static final SulfuricGas SULFURIC_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "sulfuric_gas"), new SulfuricGas());
    public static final ChloricGas CHLORIC_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "chloric_gas"), new ChloricGas());
    public static final StronglyReducingGas STRONGLY_REDUCING_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "strongly_reducing_gas"), new StronglyReducingGas());
    public static final StronglyOxidizingGas STRONGLY_OXIDIZING_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "strongly_oxidizing_gas"), new StronglyOxidizingGas());

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod fluids for" + Synchro.MOD_ID);
    }
}
