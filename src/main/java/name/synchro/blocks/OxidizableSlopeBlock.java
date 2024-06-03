package name.synchro.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableBiMap;
import name.synchro.registrations.BlocksRegistered;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Optional;
import java.util.function.Supplier;
@Deprecated
public class OxidizableSlopeBlock extends SlopeBlock implements Oxidizable{
    private static final Supplier<ImmutableBiMap<Object, Object>> SLOPE_OXIDATION_LEVEL_INCREASES = Suppliers.memoize(() -> ImmutableBiMap.builder()
            .put(BlocksRegistered.CUT_COPPER_SLOPE, BlocksRegistered.EXPOSED_CUT_COPPER_SLOPE)
            .put(BlocksRegistered.EXPOSED_CUT_COPPER_SLOPE, BlocksRegistered.WEATHERED_CUT_COPPER_SLOPE)
            .put(BlocksRegistered.WEATHERED_CUT_COPPER_SLOPE, BlocksRegistered.OXIDIZED_CUT_COPPER_SLOPE)
            .build());
    private static final Supplier<ImmutableBiMap<Object, Object>> SLOPE_OXIDATION_LEVEL_DECREASES = Suppliers.memoize(() -> (
            SLOPE_OXIDATION_LEVEL_INCREASES.get()).inverse());
    private final Oxidizable.OxidationLevel oxidationLevel;

    public OxidizableSlopeBlock(Oxidizable.OxidationLevel oxidationLevel, BlockState baseBlockState, AbstractBlock.Settings settings) {
        super(baseBlockState, settings);
        this.oxidationLevel = oxidationLevel;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    public boolean hasRandomTicks(BlockState state) {
        return getIncreasedOxidationSlope(state.getBlock()).isPresent();
    }

    private static Optional<Block> getIncreasedOxidationSlope(Block block) {
        return Optional.ofNullable((Block) SLOPE_OXIDATION_LEVEL_INCREASES.get().get(block));
    }

    @Override
    public Optional<BlockState> getDegradationResult(BlockState state) {
        return getIncreasedOxidationSlope(state.getBlock()).map((block) ->
                block.getStateWithProperties(state));
    }

    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }
}
