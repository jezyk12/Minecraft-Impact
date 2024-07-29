package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.screenHandlers.ElectricConsumerScreenHandler;
import name.synchro.screenHandlers.ElectricSourceScreenHandler;
import name.synchro.screenHandlers.MillstoneScreenHandler;
import name.synchro.screenHandlers.UniversalMeterScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<ElectricConsumerScreenHandler> ELECTRIC_LAMP_SCREEN_HANDLER =
            registerScreenHandler(Identifier.of(Synchro.MOD_ID, "electric_lamp"), ElectricConsumerScreenHandler::new);
    public static final ScreenHandlerType<ElectricSourceScreenHandler> ELECTRIC_SOURCE_SCREEN_HANDLER =
            registerScreenHandler(Identifier.of(Synchro.MOD_ID, "electric_source"), ElectricSourceScreenHandler::new);
    public static final ScreenHandlerType<UniversalMeterScreenHandler> UNIVERSAL_METER_SCREEN_HANDLER =
            registerScreenHandler(Identifier.of(Synchro.MOD_ID, "universal_meter"), UniversalMeterScreenHandler::new);
//    public static final ScreenHandlerType<GuidanceScreenHandler> GUIDANCE_SCREEN_HANDLER =
//            registerScreenHandler(Identifier.of(Synchro.MOD_ID, "introduction_screen_handler"), GuidanceScreenHandler::new);
    public static final ScreenHandlerType<MillstoneScreenHandler> MILLSTONE_SCREEN_HANDLER =
            registerScreenHandler(Identifier.of(Synchro.MOD_ID, "millstone_screen_handler"), MillstoneScreenHandler::new);

    @NotNull
    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(Identifier identifier, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, identifier,
                new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
    public static void registerAll() {
        Synchro.LOGGER.debug("Registered screen handlers for" + Synchro.MOD_ID);
    }
}
