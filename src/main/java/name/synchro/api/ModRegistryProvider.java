package name.synchro.api;

import name.synchro.Synchro;
import name.synchro.modUtilData.dataEntries.CowFeedDataEntry;
import name.synchro.modUtilData.dataEntries.FluidReaction;
import name.synchro.registrations.ModRegistries;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class ModRegistryProvider extends FabricDynamicRegistryProvider {
    protected static final String NAME = Synchro.MOD_ID + "Data";
    public ModRegistryProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static void addFluidReaction(Entries entries, String path, FluidReaction fluidReaction){
        entries.add(RegistryKey.of(ModRegistries.Keys.FLUID_REACTION, Synchro.id(path)), fluidReaction);
    }

    public static StatePredicate fluidLevelBetween(@Nullable Integer min, @Nullable Integer max){
        return new StatePredicate(List.of(
                new StatePredicate.Condition(FlowableFluid.LEVEL.getName(),
                        new StatePredicate.RangedValueMatcher(
                                Optional.ofNullable(min == null ? null : String.valueOf(min)),
                                Optional.ofNullable(max == null ? null : String.valueOf(max))))));
    }

    public static FluidReaction.Builder fluidReactionBuilder(){
        return FluidReaction.builder();
    }

    public static void addCowWorkingFeedsData(Entries entries, String path, CowFeedDataEntry entry){
        entries.add(RegistryKey.of(ModRegistries.Keys.COW_FEEDS_DATA_ENTRIES, Synchro.id(path)), entry);
    }

}
