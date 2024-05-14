package name.synchro.employment;

import name.synchro.util.NbtTags;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class BlockEntityWorkerManager implements WorkerManager {
    public final int employeeMaxCount;
    protected final Vec3d workingPosition;
    private final Set<UUID> employees = new HashSet<>();

    protected BlockEntityWorkerManager(int employeeMaxCount, Vec3d workingPosition) {
        this.employeeMaxCount = employeeMaxCount;
        this.workingPosition = workingPosition;
    }

    @Override
    public Set<UUID> getEmployeesUUID() {
        return employees;
    }

    @Override
    public void setEmployees(Set<UUID> uuids) {
        this.employees.clear();
        this.employees.addAll(uuids);
    }

    public boolean hasFullEmployee() {
        return employees.size() >= employeeMaxCount;
    }

    @Override
    public void removeEmployee(UUID uuid) {
        this.employees.remove(uuid);
    }

    @Override
    public void removeAllEmployee() {
        this.employees.clear();
    }

    @Override
    public boolean addEmployee(UUID uuid) {
        if (!hasFullEmployee()) {
            this.employees.add(uuid);
            return true;
        } else return false;
    }

    @Override
    public Vec3d getWorkingPosition() {
        return workingPosition;
    }

    @Override
    public abstract @Nullable Job providingJob();

    public void setEmploymentFromNbt(NbtCompound nbt) {
        this.employees.clear();
        int amount = nbt.getInt(NbtTags.AMOUNT);
        for (int i = 0; i < amount; i++) {
            this.employees.add(nbt.getUuid(String.valueOf(i)));
        }
    }

    public NbtCompound getEmploymentNbt() {
        NbtCompound nbt = new NbtCompound();
        int index = 0;
        for (UUID uuid : this.employees) {
            nbt.putUuid(String.valueOf(index), uuid);
            index++;
        }
        nbt.putInt(NbtTags.AMOUNT, index);
        return nbt;
    }
}
