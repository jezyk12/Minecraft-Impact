package name.synchro.mobGoals;

import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PushMillstoneGoal extends AbstractWorkingGoal{
    public PushMillstoneGoal(MobEntity mob, double speed) {
        super(mob, speed, EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStartActions(WorkingHandler workingHandler, Employer employer) {
        return false;
    }

    @Override
    public boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer) {
        return false;
    }

    @Override
    public void whenActionsStart(WorkingHandler workingHandler, Employer employer) {

    }

    @Override
    public void whenActionsStop(WorkingHandler workingHandler, @Nullable Employer employer) {

    }

    @Override
    public void tickActions(WorkingHandler workingHandler, Employer employer) {

    }
}
