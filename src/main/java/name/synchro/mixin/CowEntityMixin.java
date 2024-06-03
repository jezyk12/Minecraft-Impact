package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.employment.CowWorkingHandler;
import name.synchro.employment.Employee;
import name.synchro.registrations.ItemsRegistered;
import name.synchro.util.NbtTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Random;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements Employee {
    @Unique
    private final CowWorkingHandler workingHandler = new CowWorkingHandler((CowEntity)(Object)this);

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public CowWorkingHandler getWorkingHandler() {
        return this.workingHandler;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.getWorkingHandler().setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYER), this.world);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put(NbtTags.EMPLOYER, this.getWorkingHandler().getEmploymentNbt());
        return super.writeNbt(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.getWorkingHandler().leave();
        super.remove(reason);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (workingHandler.willingToWork()) workingHandler.workableTime--;
    }

    @WrapOperation(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Ingredient;ofItems([Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/recipe/Ingredient;"))
    private Ingredient modifyTemptItem(ItemConvertible[] items, Operation<Ingredient> original){
        ItemConvertible[] newItems = Arrays.copyOf(items, items.length + 1);
        newItems[items.length] = ItemsRegistered.FRESH_FORAGE;
        return Ingredient.ofItems(newItems);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void makeWorkable(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (player.getStackInHand(hand).isOf(ItemsRegistered.FRESH_FORAGE)){
            Random random = new Random();
            this.workingHandler.workableTime = 200;/*600*/
            player.getStackInHand(hand).decrement(1);
            if (world instanceof ServerWorld serverWorld){
                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(), 6, random.nextFloat() * 0.5, random.nextFloat() * 0.5, random.nextFloat() * 0.5, 0);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }
}
