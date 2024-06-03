package name.synchro.blockEntities;

import name.synchro.util.Liquid;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ComplexLiquidBlockEntity extends BlockEntity {
    private int temperature;
    private Map<Liquid, Integer> contents = new HashMap<>();

    public ComplexLiquidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

}
