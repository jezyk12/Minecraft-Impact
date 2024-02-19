package name.synchro.networkLink.networkBlockEntityAPI;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public interface NetworkBlockEntityProvider {
    HashSet<BlockPos> getLinkState();
    boolean setLinkState(HashSet<BlockPos> terminals);
    boolean clearLinkState();
    boolean linkUpTerminal(BlockPos pos);
    boolean linkUpTerminals(HashSet<BlockPos> posHashSet);
    boolean cutOffTerminal(BlockPos pos);
    boolean cutOffTerminals(HashSet<BlockPos> posHashSet);
    void onLinkStateChanged();
}
