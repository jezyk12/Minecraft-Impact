package name.synchro.util;

import net.minecraft.entity.LivingEntity;

import java.util.List;

public interface EntityEmployable {
    int getEmployeeMaxCount();
    List<LivingEntity> getEmployees();
    void setEmployees(List<LivingEntity> employees);
    void setEmployee(LivingEntity employee, int index);
    boolean hasEmployee();
    boolean releaseEmployee(int index);
}
