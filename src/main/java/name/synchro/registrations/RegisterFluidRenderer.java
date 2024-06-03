package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.fluids.gases.TranslucentGasRenderHandler;
import name.synchro.fluids.gases.TransparentGasRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

public final class RegisterFluidRenderer {
    public static void registerAll() {
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.WATER_VAPOR_GAS, new TranslucentGasRenderHandler(RegisterFluids.WATER_VAPOR_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.HOT_STEAM_GAS, new TransparentGasRenderHandler());
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.SULFURIC_GAS, new TranslucentGasRenderHandler(RegisterFluids.SULFURIC_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.CHLORIC_GAS, new TranslucentGasRenderHandler(RegisterFluids.CHLORIC_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.STRONGLY_REDUCING_GAS, new TranslucentGasRenderHandler(RegisterFluids.STRONGLY_REDUCING_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(RegisterFluids.STRONGLY_OXIDIZING_GAS, new TranslucentGasRenderHandler(RegisterFluids.STRONGLY_OXIDIZING_GAS));
        Synchro.LOGGER.debug("Registered mod fluid renderers for " + Synchro.MOD_ID);
    }
}
