package name.synchro.mobGoals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@Deprecated
public class MovingAroundPlayerWithGivenItem extends Goal {
    private final Item givenItem;
    protected final PathAwareEntity entity;
    private final double speed;
    @Nullable
    protected PlayerEntity closestPlayer;

    public MovingAroundPlayerWithGivenItem(PathAwareEntity entity, Item givenItem, double speed) {
        this.givenItem = givenItem;
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.closestPlayer = this.entity.world.getClosestPlayer(this.entity, 10);
        return this.closestPlayer != null && this.closestPlayer.getMainHandStack().getItem().equals(givenItem);
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        this.closestPlayer = null;
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.closestPlayer != null){
            double rotation = Math.toRadians((int) (this.entity.getWorld().getTime() % 360));
            double dx = Math.sin(rotation) * 5;
            double dz = Math.cos(rotation) * 5;
            Vec3d target = new Vec3d(this.closestPlayer.getX() + dx, this.closestPlayer.getY(), this.closestPlayer.getZ() + dz);
            double distance = this.entity.getPos().distanceTo(target);
            this.entity.getLookControl().lookAt(this.closestPlayer, this.entity.getMaxHeadRotation() + 20, this.entity.getMaxLookPitchChange());
            if (distance > 1.0){
                this.entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), speed);
            }
            else {
                this.entity.getNavigation().stop();
            }
        }
    }
}
