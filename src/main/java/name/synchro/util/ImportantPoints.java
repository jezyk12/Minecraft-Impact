package name.synchro.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ImportantPoints {
    private final World world;
    private final Set<Long> points = new HashSet<>();
    private long lastClearTime = -1;
    private final int clearTime;

    public ImportantPoints(World world, int clearTime) {
        this.world = world;
        this.clearTime = clearTime;
    }

    public void add(BlockPos pos) {
        points.add(pos.asLong());
    }

    public void forEach(Consumer<BlockPos> posConsumer) {
        if (world.getTime() - lastClearTime > clearTime) {
            clearAllUnloadedPoints();
            lastClearTime = world.getTime();
        }
        points.forEach(pos -> {
            posConsumer.accept(BlockPos.fromLong(pos));
        });
    }

    public void remove(BlockPos pos) {
        points.remove(pos.asLong());
    }

    public void clearAllUnloadedPoints(){
        points.removeIf(pos -> {
            BlockPos blockPos = BlockPos.fromLong(pos);
            return !world.getChunkManager().isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        });
    }

    public interface Provider {
        Map<Type, ImportantPoints> getImportantPoints();
    }

    public enum Type{
        EXTRA_COLLISION,;
    }
}
