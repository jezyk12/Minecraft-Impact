package name.synchro.mixin;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.employment.AbstractWorkingHandler;
import name.synchro.employment.Employee;
import name.synchro.employment.Job;
import name.synchro.mobGoals.PushMillstoneGoal;
import name.synchro.util.NbtTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements Employee {

    @Unique
    private final AbstractWorkingHandler workingHandler = new AbstractWorkingHandler(this) {
        @Override
        public Job availableJob() {
            return Job.PUSH_MILLSTONE;
        }

        @Override
        public int workingGoalPriority() {
            return 2;
        }

        @Override
        public void leave() {
            if (this.getEmployer() != null) {
                this.getEmployer().getWorkerManager().removeEmployee(this.mob.getUuid());
                for (Goal goal : this.availableJob().working.getGoals(this.mob)) {
                    if (goal instanceof PushMillstoneGoal pushMillstoneGoal && employer != null){
                        pushMillstoneGoal.release((MillstoneBlockEntity) employer);
                    }
                    this.mob.goalSelector.remove(goal);
                }
                this.employer = null;
            }
        }
    };

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AbstractWorkingHandler getWorkingHandler() {
        return this.workingHandler;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.getWorkingHandler().setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYER), this.world);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put(NbtTags.EMPLOYER, this.getWorkingHandler().getEmploymentNbt());
        return super.writeNbt(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.getWorkingHandler().leave();
        super.remove(reason);
    }
}
