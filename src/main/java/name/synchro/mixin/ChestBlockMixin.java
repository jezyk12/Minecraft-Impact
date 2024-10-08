package name.synchro.mixin;

import name.synchro.items.Cockroach;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> {
    protected ChestBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ChestBlockEntity blockEntity = (ChestBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getLootTable() == null) Cockroach.summon(blockEntity, 0.8f, world.getRegistryManager());
    }
}
