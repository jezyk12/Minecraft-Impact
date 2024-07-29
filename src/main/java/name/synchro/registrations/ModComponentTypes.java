package name.synchro.registrations;

import name.synchro.Synchro;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public final class ModComponentTypes {
    public static final ComponentType<NbtComponent> MISC_NBT = register("misc_nbt",
            (builder) -> builder.codec(NbtComponent.CODEC));

    private static <T> ComponentType<T> register(String path, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Synchro.MOD_ID, path), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered component types for" + Synchro.MOD_ID);
    }
}
