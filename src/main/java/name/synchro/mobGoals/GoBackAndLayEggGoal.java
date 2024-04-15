package name.synchro.mobGoals;

import name.synchro.blockEntities.StrawNestBlockEntity;
import name.synchro.employment.Employer;
import name.synchro.employment.WorkingHandler;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class GoBackAndLayEggGoal extends AbstractWorkingGoal {
    private long lastLayEggTime = mob.getWorld().getTime() + this.mob.getWorld().getRandom().nextInt(400);

    public GoBackAndLayEggGoal(MobEntity mob, double speed) {
        super(mob, speed, EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStartActions(WorkingHandler workingHandler, Employer employer) {
        return eggDuration() > 800L;
    }

    @Override
    public boolean shouldContinueActions(WorkingHandler workingHandler, Employer employer) {
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        boolean outOfWorkingPosition = this.mob.getPos().distanceTo(workingPosition) > 0.7d;
        return eggDuration() > 800L && eggDuration() < 1000L && outOfWorkingPosition;
    }

    @Override
    public void whenActionsStart(WorkingHandler workingHandler, Employer employer) {
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

    @Override
    public void whenActionsStop(WorkingHandler workingHandler, @Nullable Employer employer) {
        if (employer == null) return;
        if (employer instanceof StrawNestBlockEntity strawNestBlockEntity) {
            Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
            boolean outOfWorkingPosition = this.mob.getPos().distanceTo(workingPosition) > 0.7d;
            layEgg(strawNestBlockEntity, outOfWorkingPosition);
        }
        this.mob.getLookControl().lookAt(new Vec3d(0, 1, 0));
    }

    private void layEgg(StrawNestBlockEntity strawNestBlockEntity, boolean outOfWorkingPosition) {
        ItemStack eggSlot = strawNestBlockEntity.getStack(0);
        if (outOfWorkingPosition) {
            this.mob.dropItem(Items.EGG);
        }
        else if (eggSlot.isEmpty()) {
            strawNestBlockEntity.setStack(0, new ItemStack(Items.EGG));
            this.lastLayEggTime = this.mob.getWorld().getTime();
        }
        else if (eggSlot.isOf(Items.EGG) && eggSlot.getCount() < eggSlot.getItem().getMaxCount()) {
            strawNestBlockEntity.getStack(0).increment(1);
        }
        else {
            this.mob.dropItem(Items.EGG);
        }
        Random random = this.mob.getWorld().getRandom();
        lastLayEggTime = mob.getWorld().getTime() + random.nextInt(200);
        this.mob.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
        this.mob.emitGameEvent(GameEvent.ENTITY_PLACE);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tickActions(WorkingHandler workingHandler, Employer employer) {
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        this.mob.getLookControl().lookAt(workingPosition.x, workingPosition.y, workingPosition.z);
        this.mob.getNavigation().startMovingTo(workingPosition.x, workingPosition.y, workingPosition.z, this.speed);
    }

    private long eggDuration() {
        return this.mob.getWorld().getTime() - this.lastLayEggTime;
    }
}
