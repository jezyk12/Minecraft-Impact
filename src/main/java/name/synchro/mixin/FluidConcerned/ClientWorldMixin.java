package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Deprecated
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements FluidHelper.ForClientWorld {

    @Shadow @Final private PendingUpdateManager pendingUpdateManager;

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Override
    public void handleBlockUpdateWithFluid(BlockPos pos, BlockState blockState, FluidState fluidState, int flags) {
        if (!this.pendingUpdateManager.hasPendingUpdate(pos, blockState)) {
            super.setBlockState(pos, blockState, flags, 512);
            ((FluidHelper.ForWorld) this).setFluidState(pos, fluidState, flags, 512);
        }
    }
}
