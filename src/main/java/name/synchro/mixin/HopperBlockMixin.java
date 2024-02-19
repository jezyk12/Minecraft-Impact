package name.synchro.mixin;

import name.synchro.synchroItems.Cockroach;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends BlockWithEntity {
    protected HopperBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        HopperBlockEntity blockEntity = (HopperBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.lootTableId == null) Cockroach.summon(blockEntity, 0.3f);
    }
}
