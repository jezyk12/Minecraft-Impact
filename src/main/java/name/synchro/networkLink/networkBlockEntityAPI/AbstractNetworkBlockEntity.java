package name.synchro.networkLink.networkBlockEntityAPI;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractNetworkBlockEntity extends BlockEntity implements NetworkBlockEntityProvider {
    private static final String LINK = "link";
    private final HashSet<BlockPos> linkedTerminals = new HashSet<>();
    public AbstractNetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public HashSet<BlockPos> getLinkState(){
        return linkedTerminals;
    }
    public boolean setLinkState(HashSet<BlockPos> terminals){
        if (!linkedTerminals.equals(terminals)){
            clearLinkState();
            linkUpTerminals(terminals);
            return true;
        }
        else return false;
    }
    public boolean linkUpTerminal(BlockPos pos){
        if (pos.equals(this.pos)) return false;
        else return linkedTerminals.add(pos);
    }
    public boolean linkUpTerminals(HashSet<BlockPos> posHashSet){
        posHashSet.remove(this.pos);
        return linkedTerminals.addAll(posHashSet);
    }
    public boolean cutOffTerminal(BlockPos pos){
        return linkedTerminals.remove(pos);
    }
    public boolean cutOffTerminals(HashSet<BlockPos> posHashSet){
        return linkedTerminals.removeAll(posHashSet);
    }
    public boolean clearLinkState(){
        if (linkedTerminals.isEmpty()) return false;
        else {
            linkedTerminals.clear();
            return true;
        }
    }
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);
        if (nbt.contains(LINK)){
            for (Long source: nbt.getLongArray(LINK)){
                linkedTerminals.add(BlockPos.fromLong(source));
            }
        }
    }
    @Override
    protected void writeNbt(NbtCompound toWriteNbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        List<Long> terminals = new ArrayList<>();
        for (BlockPos source: linkedTerminals){
            terminals.add(source.asLong());
        }
        toWriteNbt.putLongArray(LINK,terminals);
        super.writeNbt(toWriteNbt, wrapperLookup);
    }
}
