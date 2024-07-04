package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.fluids.gases.TranslucentGasRenderHandler;
import name.synchro.fluids.gases.TransparentGasRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

public final class FluidRenderers {
    public static void registerAll() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.WATER_VAPOR_GAS, new TranslucentGasRenderHandler(ModFluids.WATER_VAPOR_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.HOT_STEAM_GAS, new TransparentGasRenderHandler());
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.SULFURIC_GAS, new TranslucentGasRenderHandler(ModFluids.SULFURIC_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.CHLORIC_GAS, new TranslucentGasRenderHandler(ModFluids.CHLORIC_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STRONGLY_REDUCING_GAS, new TranslucentGasRenderHandler(ModFluids.STRONGLY_REDUCING_GAS));
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STRONGLY_OXIDIZING_GAS, new TranslucentGasRenderHandler(ModFluids.STRONGLY_OXIDIZING_GAS));
        Synchro.LOGGER.debug("Registered mod fluid renderers for " + Synchro.MOD_ID);
    }
}
