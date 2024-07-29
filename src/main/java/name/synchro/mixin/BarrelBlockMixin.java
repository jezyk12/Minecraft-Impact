package name.synchro.mixin;

import name.synchro.items.Cockroach;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin extends BlockWithEntity {
    protected BarrelBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BarrelBlockEntity blockEntity = (BarrelBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getLootTable() == null) Cockroach.summon(blockEntity, 0.8f, world.getRegistryManager());
    }
}
