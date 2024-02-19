package name.synchro.networkLink.networkBlockAPI;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.HashSet;

public interface NetworkSearchProvider {
    HashSet<Direction> getLinkableDirections(BlockState state);

    HashSet<Direction> getLinkedDirections(BlockState state);
    boolean isNotDeadEnd();
}
