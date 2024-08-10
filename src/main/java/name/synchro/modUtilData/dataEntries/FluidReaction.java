package name.synchro.modUtilData.dataEntries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import name.synchro.Synchro;
import name.synchro.modUtilData.reactions.LocationAction;
import name.synchro.registrations.ModRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record FluidReaction(FluidPredicate fluidPredicate, BlockPredicate blockPredicate, List<LocationAction> actions) {
    public static final MapCodec<FluidReaction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidPredicate.CODEC.fieldOf("fluid_state").forGetter(FluidReaction::fluidPredicate),
            BlockPredicate.CODEC.fieldOf("block_state").forGetter(FluidReaction::blockPredicate),
            Codec.list(LocationAction.CODEC).fieldOf("actions").forGetter(FluidReaction::actions)
    ).apply(instance, FluidReaction::new));

    private static final Long2ObjectMap<FluidReaction> CACHE = new Long2ObjectOpenHashMap<>();

    public static List<LocationAction> match(ServerWorld world, BlockPos pos) {
        final long id = asLong(world.getFluidState(pos), world.getBlockState(pos));
        if (CACHE.containsKey(id)){
            FluidReaction reaction = CACHE.get(id);
            if (!reaction.blockPredicate().hasNbt()) {
                return reaction.actions();
            }
            else if (reaction.matchOne(world, pos)) {
                return reaction.actions();
            }
        }
        List<LocationAction> ret = new ArrayList<>();
        getStream(world).filter(fluidReaction -> fluidReaction.matchOne(world, pos))
                .forEach(reaction -> {
                    CACHE.put(id, reaction);
                    ret.addAll(reaction.actions());
                });
        return ret;
    }

    private static long asLong(FluidState fluidState, BlockState blockState){
        int f = Fluid.STATE_IDS.getRawId(fluidState);
        int b = Block.STATE_IDS.getRawId(blockState);
        return ((long) f << 32) | b;
    }

    private static Stream<FluidReaction> getStream(ServerWorld world){
        try {
            return world.getRegistryManager().getWrapperOrThrow(ModRegistries.Keys.FLUID_REACTION).streamEntries().map(RegistryEntry.Reference::value);
        } catch (Exception e){
            Synchro.LOGGER.error("Unable to get fluidReactions: {}", e, e);
            return Stream.<FluidReaction>builder().build();
        }
    }

    private boolean matchOne(ServerWorld world, BlockPos pos) {
        if (!fluidPredicate().test(world, pos)) return false;
        return blockPredicate().test(world, pos);
    }

    public static void onReload(){
        CACHE.clear();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private FluidPredicate fluidPredicate;
        private BlockPredicate blockPredicate;
        private final List<LocationAction> actions = new ArrayList<>();

        private Builder() {}

        public Builder fluid(FluidPredicate statePredicate){
            this.fluidPredicate = statePredicate;
            return this;
        }

        public Builder block(BlockPredicate statePredicate){
            this.blockPredicate = statePredicate;
            return this;
        }

        public Builder action(LocationAction action){
            this.actions.add(action);
            return this;
        }

        public FluidReaction build(){
            if (fluidPredicate == null || blockPredicate == null)
                throw new IllegalArgumentException("Require fluid and block for FluidReaction!");
            return new FluidReaction(fluidPredicate, blockPredicate, actions);
        }
    }
}
