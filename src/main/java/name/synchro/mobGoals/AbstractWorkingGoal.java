package name.synchro.mobGoals;

import name.synchro.employment.Employee;
import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public abstract class AbstractWorkingGoal extends Goal {
    protected final MobEntity mob;
    protected final double speed;
    @Nullable private final WorkingHandler workingHandler;
    protected long lastStartTime = -114514;
    public AbstractWorkingGoal(MobEntity mob, double speed, EnumSet<Goal.Control> controls) {
        this.mob = mob;
        if (mob instanceof Employee employee) this.workingHandler = employee.getWorkingHandler();
        else workingHandler = null;
        this.speed = speed;
        this.setControls(controls);
    }

    @Override
    public final boolean canStart() {
        if (workingHandler == null) return false;
        if (workingHandler.getEmployer() == null) return false;
        return canStartActions(this.workingHandler, this.workingHandler.getEmployer());
    }

    public abstract boolean canStartActions(WorkingHandler workingHandler, Employer employer);

    @Override
    public final boolean shouldContinue() {
        assert this.workingHandler != null;
        if (this.workingHandler.getEmployer() == null) return false;
        return shouldContinueActions(this.workingHandler, this.workingHandler.getEmployer());
    }

    public abstract boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer);

    @Override
    public final void start() {
        assert this.workingHandler != null;
        assert this.workingHandler.getEmployer() != null;
        this.lastStartTime = this.mob.getWorld().getTime();
        whenActionsStart(this.workingHandler, this.workingHandler.getEmployer());
    }

    public abstract void whenActionsStart(WorkingHandler workingHandler, Employer employer);

    @Override
    public final void stop() {
        assert this.workingHandler != null;
        whenActionsStop(this.workingHandler, this.workingHandler.getEmployer());
    }

    public abstract void whenActionsStop(WorkingHandler workingHandler, @Nullable Employer employer);

    @Override
    public final void tick() {
        assert this.workingHandler != null;
        if (this.workingHandler.getEmployer() != null) {
            tickActions(this.workingHandler, this.workingHandler.getEmployer());
        }
    }

    public abstract void tickActions(WorkingHandler workingHandler, Employer employer);
}
