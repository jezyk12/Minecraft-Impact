package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SnowballEntity.class)
public abstract class SnowballEntityMixin extends ThrownItemEntity {
    public SnowballEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }
    @ModifyArg(method = "onEntityHit",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            index = 1)
    private float onEntityHitModify(float amount, @Local Entity victim){
        float damage = amount;
        int victimFrozenTicks = victim.getFrozenTicks();
        int frozenAdder;
        if (victimFrozenTicks < 420){
            frozenAdder = 40;
        }
        else if (victimFrozenTicks < 560){
            frozenAdder = 30;
        }
        else if (victimFrozenTicks < 700){
            frozenAdder = 20;
        }
        else frozenAdder = 10;
        victim.setFrozenTicks(victimFrozenTicks + frozenAdder);
        if (victimFrozenTicks > 140){
            damage += (victimFrozenTicks - 140) / 70f + 1f;
        }
        return damage;
    }
}
