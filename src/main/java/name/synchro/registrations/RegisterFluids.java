package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.synchroBlocks.WaterVaporGas;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegisterFluids {
    public static final WaterVaporGas WATER_VAPOR_GAS = Registry.register(Registries.FLUID,
            new Identifier(Synchro.MOD_ID, "water_vapor_gas"), new WaterVaporGas());

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered mod fluids for" + Synchro.MOD_ID);
    }
}
