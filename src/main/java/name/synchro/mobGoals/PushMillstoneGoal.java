package name.synchro.mobGoals;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.employment.CowWorkingHandler;
import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;

public class PushMillstoneGoal extends AbstractWorkingGoal{
    private boolean pushing = false;
    private boolean pushingReversely = false;

    public PushMillstoneGoal(MobEntity mob, double speed) {
        super(mob, speed, EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStartActions(WorkingHandler workingHandler, Employer employer) {
        return workingHandler.willingToWork() && shouldContinueActions(workingHandler, employer);
    }

    @Override
    public boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer) {
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity){
            if (millstoneBlockEntity.isLocked()) return false;
            return millstoneBlockEntity.isFollowingRecipe();
        }
        return false;
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
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity){
            tryRelease(millstoneBlockEntity);
        }
    }

    @Override
    public void tickActions(WorkingHandler workingHandler, Employer employer) {
        if (employer instanceof MillstoneBlockEntity millstoneBlockEntity) {
            Random random = new Random();
            if ((this.mob.getWorld().getTime() - this.lastStartTime) % 20 == 0){
                int rotBlock = millstoneBlockEntity.getRecentRotation(this.mob.getWorld().getTime());
                int degreeDiff = millstoneBlockEntity.getWorkerManager().getDegreeDiff(rotBlock, this.mob.getEyePos());
                updatePushingStatus(millstoneBlockEntity, degreeDiff);
            }
            Vec3d towardPos = millstoneBlockEntity.getWorkerManager().getNextStepPos(this.mob.getPos());
            this.mob.getLookControl().lookAt(towardPos.x, towardPos.y, towardPos.z);
            this.mob.getNavigation().startMovingTo(towardPos.x, towardPos.y, towardPos.z, this.speed);
            if (workingHandler instanceof CowWorkingHandler cowWorkingHandler){
                boolean eaten = false;
                boolean hungry = false;
                if (cowWorkingHandler.workableTime < 100){
                    ItemStack feed = millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_FEED);
                    if (MillstoneBlockEntity.MILLSTONE_FEEDS.containsKey(feed.getItem())){
                        int feedTime = MillstoneBlockEntity.MILLSTONE_FEEDS.get(feed.getItem());
                        feed.decrement(1);
                        cowWorkingHandler.workableTime += feedTime - 100 + random.nextInt(200);
                        mob.getWorld().playSoundFromEntity(null, mob, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        eaten = true;
                    }
                    else if (cowWorkingHandler.workableTime <= 0){
                        tryRelease(millstoneBlockEntity);
                        cowWorkingHandler.leave();
                        hungry = true;
                    }
                }
                if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.WAX_OFF, towardPos.x, towardPos.y, towardPos.z, 1, 0.0, 0.0, 0.0, 0.0);
                    if (eaten) {
                        serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), 5, random.nextFloat() * 0.5, random.nextFloat() * 0.5, random.nextFloat() * 0.5, 0);
                    }
                    if (hungry) {
                        serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), 5, random.nextFloat() * 0.5, random.nextFloat() * 0.5, random.nextFloat() * 0.5, 0);
                    }
                }
            }
        }
    }

    private void updatePushingStatus(MillstoneBlockEntity millstoneBlockEntity, int degreeDiff) {
        if (isEffectivelyPushing(millstoneBlockEntity)) {
            tryPush(millstoneBlockEntity, degreeDiff);
        }
        else {
            tryRelease(millstoneBlockEntity);
        }
    }

    private boolean isEffectivelyPushing(MillstoneBlockEntity millstoneBlockEntity) {
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

    public void tryRelease(MillstoneBlockEntity millstoneBlockEntity) {
        if (pushing){
            if (!pushingReversely) {
                millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() - 1);
            } else {
                millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() + 1);
            }
            pushing = false;
        }
    }

    private void tryPush(MillstoneBlockEntity millstoneBlockEntity, int degreeDiff) {
        if (!pushing){
            if (degreeDiff > 0) {
                millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() + 1);
                pushing = true;
                pushingReversely = false;
            } else {
                millstoneBlockEntity.updateSpeedMultiplier(this.mob.getWorld().getTime(), millstoneBlockEntity.getRotationProvider().getSpeedMultiplier() - 1);
                pushing = true;
                pushingReversely = true;
            }
        }
    }
}
