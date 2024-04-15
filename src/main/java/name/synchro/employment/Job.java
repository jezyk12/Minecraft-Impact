package name.synchro.employment;

import name.synchro.mobGoals.GoBackAndLayEggGoal;
import name.synchro.mobGoals.KeepInWorkingAreaGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

public enum Job {
    LAY_EGGS(mob -> new Goal[]{
            new GoBackAndLayEggGoal(mob, 1.2),
            new KeepInWorkingAreaGoal(mob,2, 6)
    }),

    PUSH_MILLSTONE(mob -> new Goal[]{new KeepInWorkingAreaGoal(mob, 3,6)}),
    ;

    public final Working working;
    Job(Working working) {
        this.working = working;
    }

    @FunctionalInterface
    public interface Working{
        Goal[] getGoals(MobEntity mob);
    }
}

