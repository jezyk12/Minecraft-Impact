package name.synchro.mobGoals;

import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class KeepInWorkingAreaGoal extends AbstractWorkingGoal {
    private final double minRadius;
    private final double maxRadius;

    public KeepInWorkingAreaGoal(MobEntity mob, double minRadius, double maxRadius) {
        super(mob, 1.1, EnumSet.of(Control.MOVE, Control.LOOK));
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    @Override
    public boolean canStartActions(WorkingHandler workingHandler, Employer employer) {
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        return mob.getPos().distanceTo(workingPosition) > maxRadius;
    }

    @Override
    public boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer) {
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        return mob.getPos().distanceTo(workingPosition) > minRadius && this.mob.getWorld().getTime() - this.lastStartTime < 600L;
    }

    @Override
    public void whenActionsStart(WorkingHandler workingHandler, Employer employer) {
        if (mob.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
            Vec3d path = workingPosition.subtract(mob.getPos());
            double distance = path.length();
            Vec3d pathNormalized = path.normalize();
            for (double i = 0; i < distance; i += 0.5) {
                serverWorld.spawnParticles(ParticleTypes.WAX_ON, mob.getX() + pathNormalized.x * i, mob.getY() + pathNormalized.y * i, mob.getZ() + pathNormalized.z * i, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void whenActionsStop(WorkingHandler workingHandler, @Nullable Employer employer) {
        this.mob.getNavigation().stop();
    }

    @Override
    public void tickActions(WorkingHandler workingHandler, Employer employer) {
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        this.mob.getLookControl().lookAt(workingPosition.x, workingPosition.y, workingPosition.z);
        this.mob.getNavigation().startMovingTo(workingPosition.x, workingPosition.y, workingPosition.z, this.speed);
    }

}
