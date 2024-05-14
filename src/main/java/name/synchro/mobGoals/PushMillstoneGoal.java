package name.synchro.mobGoals;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PushMillstoneGoal extends AbstractWorkingGoal{
    private boolean pushing = false;
    private boolean pushingBack = false;

    public PushMillstoneGoal(MobEntity mob, double speed) {
        super(mob, speed, EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStartActions(WorkingHandler workingHandler, Employer employer) {
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity){
            @Nullable ItemStack processing = MillstoneBlockEntity.MILLSTONE_RECIPES
                    .get(millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_INPUT).getItem());
            ItemStack products = millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_OUTPUT);
            return processing != null && MillstoneBlockEntity.canProcess(products.copy(), processing.copy());
        }
        return false;
    }

    @Override
    public boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer) {
        return canStartActions(workingHandler, employer);
    }

    @Override
    public void whenActionsStart(WorkingHandler workingHandler, Employer employer) {
        if (employer instanceof MillstoneBlockEntity){
            if (mob.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
                Vec3d path = workingPosition.subtract(mob.getPos());
                double distance = path.length();
                Vec3d pathNormalized = path.normalize();
                for (double i = 0; i < distance; i += 0.25) {
                    serverWorld.spawnParticles(ParticleTypes.GLOW, mob.getX() + pathNormalized.x * i, mob.getY() + pathNormalized.y * i, mob.getZ() + pathNormalized.z * i, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void whenActionsStop(WorkingHandler workingHandler, @Nullable Employer employer) {
        this.mob.getNavigation().stop();
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity && pushing){
            release(millstoneBlockEntity);
        }
    }

    @Override
    public void tickActions(WorkingHandler workingHandler, Employer employer) {
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity) {
            if ((this.mob.getWorld().getTime() - this.lastStartTime) % 20 == 0){
                int rotBlock = millstoneBlockEntity.getRecentRotation(this.mob.getWorld().getTime());
                int degreeDiff = millstoneBlockEntity.getWorkerManager().getDegreeDiff(rotBlock, this.mob.getEyePos());
                if (!pushing) {
                    if (isPushing(millstoneBlockEntity)) {
                        push(millstoneBlockEntity, degreeDiff);
                    }
                }
                else {
                    if (!isPushing(millstoneBlockEntity)) {
                        release(millstoneBlockEntity);
                    }
                }
            }
            Vec3d towardPos = millstoneBlockEntity.getWorkerManager().getNextStepPos(this.mob.getPos());
            this.mob.getLookControl().lookAt(towardPos.x, towardPos.y, towardPos.z);
            this.mob.getNavigation().startMovingTo(towardPos.x, towardPos.y, towardPos.z, this.speed);
            if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, towardPos.x, towardPos.y, towardPos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private boolean isPushing(MillstoneBlockEntity millstoneBlockEntity) {
        double avgWidth = (this.mob.getBoundingBox().getXLength() + this.mob.getBoundingBox().getZLength()) / 2;
        Vec3d centerPos = millstoneBlockEntity.getPos().toCenterPos();
        Vec3d eyePos = this.mob.getEyePos();
        Vec3d centerDiff = eyePos.subtract(centerPos);
        double r = Math.sqrt(centerDiff.x * centerDiff.x + centerDiff.z * centerDiff.z);
        Vec3d polePos = millstoneBlockEntity.getWorkerManager().polePos(r);
        Vec3d poleDiff = eyePos.subtract(polePos);
        double d = Math.sqrt(poleDiff.x * poleDiff.x + poleDiff.z * poleDiff.z) - avgWidth;
        return d <= 0.5
                && Math.abs(this.mob.getY() - millstoneBlockEntity.getPos().getY()) < 2.0
                && eyePos.isInRange(centerPos, 3.0);
    }

    public void release(MillstoneBlockEntity millstoneBlockEntity) {
        if (!pushingBack) {
            millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() - 1);
        }
        else {
            millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() + 1);
        }
        pushing = false;
    }

    private void push(MillstoneBlockEntity millstoneBlockEntity, int degreeDiff) {
        if (degreeDiff > 0) {
            millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() + 1);
            pushing = true;
            pushingBack = false;
        }
        else {
            millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() - 1);
            pushing = true;
            pushingBack = true;
        }
    }
}
