package name.synchro.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FluidStateArgType implements ArgumentType<FluidState> {
    private static final Collection<String> EXAMPLES = Arrays.asList("water", "minecraft:water", "flowing_water[level=1]");
    private final RegistryWrapper<Fluid> registryWrapper;
    public FluidStateArgType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.getWrapperOrThrow(RegistryKeys.FLUID);
    }

    @Override
    public FluidState parse(StringReader reader) throws CommandSyntaxException {
        return FluidStateArgParser.fluid(this.registryWrapper, reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return FluidStateArgParser.getSuggestions(this.registryWrapper, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
