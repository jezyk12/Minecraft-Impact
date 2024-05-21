package name.synchro.employment;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.mobGoals.KeepInWorkingAreaGoal;
import name.synchro.mobGoals.PushMillstoneGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.CowEntity;

import java.util.HashSet;
import java.util.Set;

public class CowWorkingHandler extends AbstractWorkingHandler {
    public CowWorkingHandler(CowEntity cow) {
        super(cow);
    }

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
            Set<Goal> toRemoveGoals = new HashSet<>();
            for (PrioritizedGoal prioritizedGoal : this.mob.goalSelector.getGoals()) {
                Goal goal = prioritizedGoal.getGoal();
                if (goal instanceof PushMillstoneGoal pushMillstoneGoal) {
                    pushMillstoneGoal.tryRelease((MillstoneBlockEntity) (this.getEmployer()));
                    toRemoveGoals.add(goal);
                }
                else if (goal instanceof KeepInWorkingAreaGoal) {
                    toRemoveGoals.add(goal);
                }
            }
            toRemoveGoals.forEach(this.mob.goalSelector::remove);
            this.employer = null;
        }
    }

    @Override
    public boolean willingToWork() {
        return workableTime > 0;
    }
}
