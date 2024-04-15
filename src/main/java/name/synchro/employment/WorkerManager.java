package name.synchro.employment;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface WorkerManager {
    Set<UUID> getEmployeesUUID();

    void setEmployees(Set<UUID> uuids);

    void removeEmployee(UUID uuid);

    void removeAllEmployee();

    boolean addEmployee(UUID uuid);

    Vec3d getWorkingPosition();

    @Nullable Job providingJob();

}
