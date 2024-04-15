package name.synchro.employment;

import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public class ChickenWorkingHandler extends AbstractWorkingHandler {

    public ChickenWorkingHandler(MobEntity mob) {
        super(mob);
    }

    @Override
    public @Nullable Job availableJob() {
        return Job.LAY_EGGS;
    }

    @Override
    public int workingGoalPriority() {
        return 2;
    }

}
