package name.synchro.mixin;

import name.synchro.employment.AbstractWorkingHandler;
import name.synchro.employment.Employee;
import name.synchro.employment.Job;
import net.minecraft.entity.EntityType;
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
        this.getWorkingHandler().setEmploymentFromNbt(nbt.getCompound(Employee.EMPLOYER), this.world);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put(Employee.EMPLOYER, this.getWorkingHandler().getEmploymentNbt());
        return super.writeNbt(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.getWorkingHandler().leave();
        super.remove(reason);
    }
}
