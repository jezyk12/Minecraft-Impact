package name.synchro.entities;

import name.synchro.mixin.accessor.MobEntityAccessor;
import name.synchro.registrations.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DuckEntity extends AnimalEntity {
    public DuckEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        goalSelector().add(0, new SwimGoal(this));
        goalSelector().add(1, new EscapeDangerGoal(this, 1.4));
        goalSelector().add(2, new AnimalMateGoal(this, 1.0));
        goalSelector().add(3, new TemptGoal(this, 1.0, this::isBreedingItem, false));
        goalSelector().add(4, new FollowParentGoal(this, 1.1));
        goalSelector().add(5, new WanderAroundFarGoal(this, 1.0));
        goalSelector().add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        goalSelector().add(7, new LookAroundGoal(this));
    }

    private GoalSelector goalSelector() {
        return ((MobEntityAccessor) this).getGoalSelector();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.CHICKEN_FOOD);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.DUCK.create(world);
    }
}
