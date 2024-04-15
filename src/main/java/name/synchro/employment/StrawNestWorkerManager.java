package name.synchro.employment;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class StrawNestWorkerManager extends BlockEntityWorkerManager {
    public StrawNestWorkerManager(int employeeMaxCount, Vec3d workingPosition){
        super(employeeMaxCount, workingPosition);

    }

    public StrawNestWorkerManager(int employeeMaxCount, BlockPos blockPos){
        super(employeeMaxCount, blockPos.toCenterPos());
    }

    @Override
    public @Nullable Job providingJob() {
        return Job.LAY_EGGS;
    }
}
