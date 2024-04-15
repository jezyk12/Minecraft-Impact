package name.synchro.employment;

import name.synchro.Synchro;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWorkingHandler implements WorkingHandler {
    @Nullable
    private Employer employer;
    private final MobEntity mob;

    protected AbstractWorkingHandler(MobEntity mob) {
        this.mob = mob;
    }

    @Override
    @Nullable
    public Employer getEmployer() {
        return this.employer;
    }

    public void setEmploymentFromNbt(NbtCompound nbt, World world) {
        if (nbt.contains(Employee.TYPE_BLOCK)) {
            BlockPos blockPos = BlockPos.fromLong(nbt.getLong(Employee.TYPE_BLOCK));
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof Employer blockEmployer) this.setEmployer(blockEmployer);
            else Synchro.LOGGER.warn("Cannot identify a BlockEntity as an employer at " + blockPos);
        } else if (nbt.contains(Employee.TYPE_ENTITY)) {
            Synchro.LOGGER.warn("Uncompleted feature: Entity as an employer");
        } else this.employer = null;
    }

    public NbtCompound getEmploymentNbt() {
        NbtCompound nbt = new NbtCompound();
        if (this.employer != null){
            if (this.employer instanceof BlockEntity blockEntity){
                nbt.putLong(Employee.TYPE_BLOCK, blockEntity.getPos().asLong());
            }
            else if (this.employer instanceof Entity){
                Synchro.LOGGER.warn("Uncompleted feature: Entity as an employer");
            }
            else Synchro.LOGGER.warn("Unacceptable employer type");
        }
        return nbt;
    }

    @Override
    public boolean isInEmployment() {
        return employer != null;
    }

    @Override
    public boolean join(Employer employer) {
        if (this.availableJob().equals(employer.getWorkerManager().providingJob())
                && employer.getWorkerManager().addEmployee(this.mob.getUuid())) {
            this.setEmployer(employer);
            return true;
        }
        return false;
    }

    private void setEmployer(Employer employer){
        this.employer = employer;
        for (Goal goal : this.availableJob().working.getGoals(this.mob)) {
            this.mob.goalSelector.add(this.workingGoalPriority(), goal);
        }
    }

    @Override
    public void leave() {
        if (this.employer != null) {
            this.employer.getWorkerManager().removeEmployee(this.mob.getUuid());
            for (Goal goal : this.availableJob().working.getGoals(this.mob)) {
                this.mob.goalSelector.remove(goal);
            }
            this.employer = null;
        }
    }

    @Override
    public abstract Job availableJob();

    @Override
    public abstract int workingGoalPriority();
}
