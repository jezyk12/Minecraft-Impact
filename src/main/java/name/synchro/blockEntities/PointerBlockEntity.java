package name.synchro.blockEntities;

import name.synchro.registrations.RegisterBlockEntities;
import name.synchro.util.NbtTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PointerBlockEntity extends BlockEntity {
    @Nullable
    private CentralBlockEntity referenceBlockEntity;
    @Nullable
    private BlockPos referencePos;
    public PointerBlockEntity(CentralBlockEntity referenceBlockEntity, BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.POINTER_BLOCK_ENTITY, pos, state);
        this.referenceBlockEntity = referenceBlockEntity;
        this.referencePos = referenceBlockEntity.getPos();
    }

    public PointerBlockEntity(@Nullable BlockPos referencePos, BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.POINTER_BLOCK_ENTITY, pos, state);
        this.referencePos = referencePos;
        if (referencePos != null && world != null && world.getBlockEntity(referencePos) instanceof CentralBlockEntity centralBlockEntity){
            this.referenceBlockEntity = centralBlockEntity;
        }
    }

    public PointerBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.POINTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(NbtTags.REFERENCE)){
            NbtCompound pos = nbt.getCompound(NbtTags.REFERENCE);
            this.referencePos = NbtHelper.toBlockPos(pos);
            if (world != null && world.getBlockEntity(this.referencePos) instanceof CentralBlockEntity centralBlockEntity){
                this.referenceBlockEntity = centralBlockEntity;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (this.referencePos != null){
            NbtCompound pos = NbtHelper.fromBlockPos(this.referencePos);
            nbt.put(NbtTags.REFERENCE, pos);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (this.referencePos != null && world != null && world.getBlockEntity(referencePos) instanceof CentralBlockEntity centralBlockEntity) {
            this.referenceBlockEntity = centralBlockEntity;
        }
    }

    public CentralBlockEntity refer(){
        return this.referenceBlockEntity;
    }
}
