package name.synchro.modUtilData.dataEntries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.modUtilData.reactions.LocationAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.Optional;

public record FluidReaction(Fluid fluid, Optional<StatePredicate> fluidStatePredicate, Block block,
                            Optional<StatePredicate> blockStatePredicate, List<LocationAction> actions) {
    public static final MapCodec<FluidReaction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Registries.FLUID.getCodec().fieldOf("fluid").forGetter(FluidReaction::fluid),
            StatePredicate.CODEC.optionalFieldOf("fluid_state_conditions").forGetter(FluidReaction::fluidStatePredicate),
            Registries.BLOCK.getCodec().fieldOf("block").forGetter(FluidReaction::block),
            StatePredicate.CODEC.optionalFieldOf("block_state_conditions").forGetter(FluidReaction::blockStatePredicate),
            Codec.list(LocationAction.CODEC).fieldOf("actions").forGetter(FluidReaction::actions)
    ).apply(instance, FluidReaction::new));

    public boolean test(FluidState fluidState, BlockState blockState) {
        if (fluidStatePredicate().isPresent()) {
            if (!fluidStatePredicate().get().test(fluidState)) return false;
        }
        if (blockStatePredicate().isPresent()) {
            return blockStatePredicate().get().test(blockState);
        }
        return true;
    }
}
