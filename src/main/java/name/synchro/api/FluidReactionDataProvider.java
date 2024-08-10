package name.synchro.api;

import com.google.common.annotations.VisibleForTesting;
import name.synchro.Synchro;
import name.synchro.modUtilData.ModDataLoader;
import name.synchro.modUtilData.dataEntries.FluidReactionData;
import name.synchro.modUtilData.reactions.LocationAction;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class FluidReactionDataProvider extends FabricCodecDataProvider<FluidReactionData.Entry> {
    protected static final String NAME = "Fluid Reaction";
    final Map<String, FluidReactionData.Entry> entries = new HashMap<>();

    protected FluidReactionDataProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture, DataOutput.OutputType.DATA_PACK,
                ModDataLoader.DICTIONARY_ROOT + "/" + FluidReactionData.FOLDER,
                FluidReactionData.Entry.CODEC.codec());
    }

    @Override
    protected void configure(BiConsumer<Identifier, FluidReactionData.Entry> provider, RegistryWrapper.WrapperLookup lookup) {
        generate(lookup);
        for (Map.Entry<String, FluidReactionData.Entry> entry : entries.entrySet()) {
            Identifier filePath = Identifier.of(Synchro.MOD_ID, entry.getKey());
            provider.accept(filePath, entry.getValue());
        }
        this.entries.clear();
    }

    public abstract void generate(RegistryWrapper.WrapperLookup wrapperLookup);

    @VisibleForTesting
    public void addEntry(String fileName, FluidReactionData.Entry entry){
        this.entries.put(fileName, entry);
    }

    public Optional<StatePredicate> fluidLevelBetween(int min, int max){
        return Optional.of(new StatePredicate(List.of(
                new StatePredicate.Condition(FlowableFluid.LEVEL.getName(),
                        new StatePredicate.RangedValueMatcher(Optional.of(String.valueOf(min)), Optional.of(String.valueOf(max)))))));
    }

    public Builder builder(String prefix){
        return new Builder(prefix + "_");
    }

    public Builder builder(){
        return new Builder("");
    }

    public class Builder{
        private String prefix;
        private final Set<Block> blocks = new HashSet<>();
        private final Set<Fluid> fluids = new HashSet<>();
        @Nullable private StatePredicate fluidPredicate = null;
        @Nullable private StatePredicate blockPredicate = null;
        private final List<LocationAction> actions = new ArrayList<>();

        private Builder(String prefix) {
            this.prefix = prefix;
        }

        public Builder block(Block... blocks){
            this.blocks.addAll(Set.of(blocks));
            return this;
        }

        public Builder fluid(Fluid... fluids){
            this.fluids.addAll(Set.of(fluids));
            return this;
        }

        public Builder fluidCondition(StatePredicate statePredicate){
            this.fluidPredicate = statePredicate;
            return this;
        }

        public Builder blockCondition(StatePredicate statePredicate){
            this.blockPredicate = statePredicate;
            return this;
        }

        public Builder action(LocationAction action){
            this.actions.add(action);
            return this;
        }

         public void buildAndApply(){
            for (Block block : this.blocks) for (Fluid fluid :this.fluids) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier fluidId = Registries.FLUID.getId(fluid);
                String fileName = this.prefix + (blockId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? "" : blockId.getNamespace() + "_") + blockId.getPath()
                        + "_with_" + (blockId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? "" : blockId.getNamespace() + "_") + fluidId.getPath();
                FluidReactionDataProvider.this.entries.put(fileName,
                        new FluidReactionData.Entry(fluid, Optional.ofNullable(fluidPredicate),
                                block, Optional.ofNullable(blockPredicate), actions));
            }
         }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
