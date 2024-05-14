package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.employment.ChickenWorkingHandler;
import name.synchro.employment.Employee;
import name.synchro.util.NbtTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity implements Employee {

    @Unique protected ChickenWorkingHandler workingHandler = new ChickenWorkingHandler(this);

    protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ChickenWorkingHandler getWorkingHandler() {
        return this.workingHandler;
    }

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/ChickenEntity;isAlive()Z", ordinal = 0))
    private boolean layEggCondition(ChickenEntity instance, Operation<Boolean> original) {
        return !this.getWorkingHandler().isInEmployment() && original.call(instance);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readAdditionalNbt(NbtCompound nbt, CallbackInfo ci){
        this.getWorkingHandler().setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYER), this.world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeAdditionalNbt(NbtCompound toWriteNbt, CallbackInfo ci){
        toWriteNbt.put(NbtTags.EMPLOYER, this.getWorkingHandler().getEmploymentNbt());
    }

    @Override
    public void remove(RemovalReason reason) {
        this.getWorkingHandler().leave();
        super.remove(reason);
    }
}
