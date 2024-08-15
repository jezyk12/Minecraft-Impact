package name.synchro.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public abstract class CentralBlockEntity<T> extends BlockEntity {
    private Set<BlockPos> transferablePos = new HashSet<>();

    public CentralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addTransferablePos(BlockPos pos) {
        transferablePos.add(pos);
    }

    public void removeTransferablePos(BlockPos pos) {
        transferablePos.remove(pos);
    }

    public boolean transfer(BlockPos pos, boolean removeOrigin) {
        if (transferablePos.contains(pos) && world != null){
            if (world.getBlockEntity(pos) instanceof PointerBlockEntity pointerBlockEntity){
                world.removeBlockEntity(pos);
                BlockEntity copy = BlockEntity.createFromNbt(pos, world.getBlockState(pos), this.createNbt());
                world.addBlockEntity(copy);
                if (removeOrigin) {
                    world.removeBlockEntity(this.getPos());
                }
                else {
                                    }
            }
            return true;
        }
        return false;
    }

}
