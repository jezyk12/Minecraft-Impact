package name.synchro.dataGeneration;

import com.mojang.datafixers.util.Pair;
import name.synchro.api.ModRegistryProvider;
import name.synchro.modUtilData.reactions.ActiveEvents;
import name.synchro.modUtilData.reactions.SetBlock;
import name.synchro.registrations.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluids;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.WorldEvents;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class IntrinsicModData extends ModRegistryProvider {
    public IntrinsicModData(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        addFluidReaction(entries, "burn_wooden_stairs_flowing", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.FLOWING_LAVA).state(fluidLevelBetween(4, null)).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_STAIRS).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_STAIRS.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_stairs_still", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.LAVA).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_STAIRS).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_STAIRS.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_slab_down_flowing", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.FLOWING_LAVA).state(fluidLevelBetween(3, null)).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_SLABS).state(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.BOTTOM)).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_slab_down_still", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.LAVA).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_SLABS).state(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.BOTTOM)).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_slab_up_flowing", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.FLOWING_LAVA).state(fluidLevelBetween(6, null)).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_SLABS).state(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.TOP)).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_slab_up_still", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.LAVA).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_SLABS).state(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.TOP)).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_fence_flowing", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.FLOWING_LAVA).state(fluidLevelBetween(4, null)).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_FENCES).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_FENCE.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
        addFluidReaction(entries, "burn_wooden_fence_still", fluidReactionBuilder()
                .fluid(FluidPredicate.Builder.create().fluid(Fluids.LAVA).build())
                .block(BlockPredicate.Builder.create().tag(BlockTags.WOODEN_FENCES).build())
                .action(SetBlock.builder(ModBlocks.BURNT_CHARCOAL_FENCE.getDefaultState()).followProperties().build())
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .build());
    }
}
