package name.synchro.blockEntities;

import name.synchro.registrations.RegisterBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class DebugBlockEntity extends BlockEntity {
    public DebugBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DebugBlockEntity(BlockPos pos, BlockState state){
        this(RegisterBlockEntities.DEBUG_BLOCK_ENTITY, pos, state);
    }

}
