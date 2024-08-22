package name.synchro.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FluidStateArgParser {
    protected static final DynamicCommandExceptionType INVALID_FLUID_ID_EXCEPTION = new DynamicCommandExceptionType(fluid ->
            Text.stringifiedTranslatable("argument.synchro.fluid.id.unknown", fluid));
    protected static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
            (fluid, property) -> Text.stringifiedTranslatable("argument.synchro.fluid.property.unknown", fluid, property));
    protected static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
            (block, property) -> Text.stringifiedTranslatable("argument.synchro.fluid.property.duplicate", property, block));
    public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("argument.synchro.fluid.property.unclosed"));
    public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
            (fluid, property) -> Text.stringifiedTranslatable("argument.synchro.fluid.property.empty", fluid, property));
    public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType((fluid, property, value) -> Text.stringifiedTranslatable("argument.synchro.fluid.property.invalid", fluid, value, property));
    protected static final char PROPERTIES_OPENING = '[';
    protected static final char PROPERTIES_CLOSING = ']';
    protected static final char PROPERTY_DEFINER = '=';
    protected static final char PROPERTY_SEPARATOR = ',';
    protected static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;

    private final RegistryWrapper<Fluid> registryWrapper;
    private final StringReader reader;
    private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
    private Identifier fluidId = Identifier.ofVanilla("");
    @Nullable
    private StateManager<Fluid, FluidState> stateFactory;
    @Nullable
    private FluidState fluidState;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    private FluidStateArgParser(RegistryWrapper<Fluid> registryWrapper, StringReader reader) {
        this.suggestions = SUGGEST_DEFAULT;
        this.registryWrapper = registryWrapper;
        this.reader = reader;
    }

    public static FluidState fluid(RegistryWrapper<Fluid> registryWrapper, String string) throws CommandSyntaxException {
        return fluid(registryWrapper, new StringReader(string));
    }

    public static FluidState fluid(RegistryWrapper<Fluid> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            FluidStateArgParser parser = new FluidStateArgParser(registryWrapper, reader);
            parser.parse();
            return parser.fluidState;
        } catch (CommandSyntaxException e) {
            reader.setCursor(i);
            throw e;
        }
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestFluidIds;
        this.parseFluidId();
        this.suggestions = this::suggestPropertiesBeginning;
        if (this.reader.canRead() && this.reader.peek() == PROPERTIES_OPENING) {
            this.parseProperties();
        }
    }

    public static CompletableFuture<Suggestions> getSuggestions(RegistryWrapper<Fluid> registryWrapper, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        FluidStateArgParser fluidStateArgParser = new FluidStateArgParser(registryWrapper, stringReader);
        try {
            fluidStateArgParser.parse();
        } catch (CommandSyntaxException ignored) {}

        return fluidStateArgParser.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private CompletableFuture<Suggestions> suggestPropertiesBeginning(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            if (this.stateFactory != null && !this.stateFactory.getProperties().isEmpty()) {
                builder.suggest(String.valueOf('['));
            }
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestPropertiesOrEnd(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(PROPERTIES_CLOSING));
        }
        return this.suggestFluidProperties(builder);
    }

    private CompletableFuture<Suggestions> suggestFluidProperties(SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        if (this.fluidState != null) {
            for (Property<?> property : this.fluidState.getProperties()) {
                if (!this.properties.containsKey(property) && property.getName().startsWith(string)) {
                    builder.suggest(property.getName() + "=");
                }
            }
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEqualsCharacter(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(PROPERTY_DEFINER));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(']'));
        }
        if (this.fluidState != null && builder.getRemaining().isEmpty() && this.properties.size() < this.fluidState.getProperties().size()) {
            builder.suggest(String.valueOf(','));
        }
        return builder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder suggestPropertyValues(SuggestionsBuilder builder, Property<T> property) {
        for (T t : property.getValues()) {
            if (t instanceof Integer integer) {
                builder.suggest(integer);
            } else {
                builder.suggest(property.name(t));
            }
        }
        return builder;
    }

    private CompletableFuture<Suggestions> suggestFluidIds(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    private void parseFluidId() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        this.fluidId = Identifier.fromCommandInput(this.reader);
        Fluid fluid = this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.FLUID, this.fluidId))
                .orElseThrow(() -> {
                    this.reader.setCursor(i);
                    return INVALID_FLUID_ID_EXCEPTION.createWithContext(this.reader, this.fluidId.toString());
                }).value();
        this.stateFactory = fluid.getStateManager();
        this.fluidState = fluid.getDefaultState();
    }

    private void parseProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestPropertiesOrEnd;
        this.reader.skipWhitespace();
        while(this.reader.canRead() && this.reader.peek() != PROPERTIES_CLOSING) {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String string = this.reader.readString();
            Property<?> property = this.stateFactory != null ? this.stateFactory.getProperty(string) : null;
            if (property == null) {
                this.reader.setCursor(i);
                throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext(this.reader, this.fluidId.toString(), string);
            }
            if (this.properties.containsKey(property)) {
                this.reader.setCursor(i);
                throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.fluidId.toString(), string);
            }
            this.reader.skipWhitespace();
            this.suggestions = this::suggestEqualsCharacter;
            if (this.reader.canRead() && this.reader.peek() == PROPERTY_DEFINER) {
                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (builder) -> suggestPropertyValues(builder, property).buildFuture();
                int j = this.reader.getCursor();
                this.parsePropertyValue(property, this.reader.readString(), j);
                this.suggestions = this::suggestCommaOrEnd;
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }
                if (this.reader.peek() == PROPERTY_SEPARATOR) {
                    this.reader.skip();
                    this.suggestions = this::suggestPropertiesBeginning;
                    continue;
                }
                if (this.reader.peek() != PROPERTIES_CLOSING) {
                    throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
                }
                break;
            }
            throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.fluidId.toString(), string);
        }
        if (this.reader.canRead()) {
            this.reader.skip();
        } else {
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
        }
    }

    private <T extends Comparable<T>> void parsePropertyValue(Property<T> property, String value, int cursor) throws CommandSyntaxException {
        Optional<T> optional = property.parse(value);
        if (optional.isPresent()) {
            if (this.fluidState != null) {
                this.fluidState = this.fluidState.with(property, optional.get());
            }
            this.properties.put(property, optional.get());
        } else {
            this.reader.setCursor(cursor);
            throw INVALID_PROPERTY_EXCEPTION.createWithContext(this.reader, this.fluidId.toString(), property.getName(), value);
        }
    }
//    private CompletableFuture<Suggestions> suggestBlockOrTagId(SuggestionsBuilder builder) {
//        this.suggestIdentifiers(builder);
//        this.suggestFluidIds(builder);
//        return builder.buildFuture();
//    }

//    public static String stringifyBlockState(BlockState state) {
//        StringBuilder stringBuilder = new StringBuilder((String)state.getRegistryEntry().getKey().map((key) -> {
//            return key.getValue().toString();
//        }).orElse("air"));
//        if (!state.getProperties().isEmpty()) {
//            stringBuilder.append('[');
//            boolean bl = false;
//
//            for(Iterator var3 = state.getEntries().entrySet().iterator(); var3.hasNext(); bl = true) {
//                Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var3.next();
//                if (bl) {
//                    stringBuilder.append(',');
//                }
//
//                stringifyProperty(stringBuilder, (Property)entry.getKey(), (Comparable)entry.getValue());
//            }
//
//            stringBuilder.append(']');
//        }
//
//        return stringBuilder.toString();
//    }

//    private static <T extends Comparable<T>> void stringifyProperty(StringBuilder builder, Property<T> property, Comparable<?> value) {
//        builder.append(property.getName());
//        builder.append('=');
//        builder.append(property.name(value));
//    }
}
