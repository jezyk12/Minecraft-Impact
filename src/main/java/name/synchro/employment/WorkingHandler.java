package name.synchro.employment;

import org.jetbrains.annotations.Nullable;

public interface WorkingHandler {
    @Nullable Employer getEmployer();

    boolean isInEmployment();

    boolean join(Employer employer);

    void leave();

    Job availableJob();

    int workingGoalPriority();

}
