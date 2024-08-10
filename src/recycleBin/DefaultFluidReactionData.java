package name.synchro.dataGeneration;

import com.mojang.datafixers.util.Pair;
import name.synchro.api.FluidReactionDataProvider;
import name.synchro.modUtilData.reactions.ActiveEvents;
import name.synchro.modUtilData.reactions.SetBlock;
import name.synchro.registrations.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluids;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.WorldEvents;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultFluidReactionData extends FluidReactionDataProvider {
    public DefaultFluidReactionData(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder("bottom").block(Blocks.OAK_SLAB, Blocks.BIRCH_SLAB, Blocks.SPRUCE_SLAB, Blocks.ACACIA_SLAB,
                        Blocks.JUNGLE_SLAB, Blocks.DARK_OAK_SLAB, Blocks.MANGROVE_SLAB, Blocks.CHERRY_SLAB,
                        Blocks.BAMBOO_SLAB, Blocks.BAMBOO_MOSAIC_SLAB)
                .fluid(Fluids.FLOWING_LAVA, Fluids.LAVA)
                .blockCondition(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.BOTTOM).build().get())
                .action(new SetBlock(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState(), false, true))
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .buildAndApply();
        this.builder("top").block(Blocks.OAK_SLAB, Blocks.BIRCH_SLAB, Blocks.SPRUCE_SLAB, Blocks.ACACIA_SLAB,
                        Blocks.JUNGLE_SLAB, Blocks.DARK_OAK_SLAB, Blocks.MANGROVE_SLAB, Blocks.CHERRY_SLAB,
                        Blocks.BAMBOO_SLAB, Blocks.BAMBOO_MOSAIC_SLAB)
                .fluid(Fluids.FLOWING_LAVA, Fluids.LAVA)
                .blockCondition(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.TOP).build().get())
                .fluidCondition(fluidLevelBetween(4, Integer.MAX_VALUE).get())
                .action(new SetBlock(ModBlocks.BURNT_CHARCOAL_SLAB.getDefaultState(), false, true))
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .buildAndApply();
        this.builder().block(Blocks.OAK_STAIRS, Blocks.BIRCH_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.ACACIA_STAIRS,
                        Blocks.JUNGLE_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.MANGROVE_STAIRS, Blocks.CHERRY_STAIRS,
                        Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS)
                .fluid(Fluids.FLOWING_LAVA, Fluids.LAVA)
                .action(new SetBlock(ModBlocks.BURNT_CHARCOAL_STAIRS.getDefaultState(), false, true))
                .action(new ActiveEvents(List.of(Pair.of(WorldEvents.LAVA_EXTINGUISHED, 0))))
                .buildAndApply();
    }
}
