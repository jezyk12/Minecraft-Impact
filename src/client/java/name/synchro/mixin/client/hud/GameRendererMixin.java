package name.synchro.mixin.client.hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.util.FocusingProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Final
    @Shadow MinecraftClient client;

    @Unique private static final double MAX_DISTANCE = 64.0;

    @WrapOperation(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"))
    private double modifyRayCastDistance(double rBlock, double rEntity, Operation<Double> original){
        return Math.max(original.call(rBlock, rEntity), MAX_DISTANCE);
    }

    @WrapOperation(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private @Nullable EntityHitResult updateFocusedTarget(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance, Operation<EntityHitResult> original){
        EntityHitResult entityHitResult = original.call(entity, min, max, box, predicate, maxDistance);
        if (entityHitResult != null) {
            ((FocusingProvider)(this.client)).synchro$setFocusedEntity(entityHitResult.getEntity());
        }
        else ((FocusingProvider)(this.client)).synchro$setFocusedEntity(null);
        return entityHitResult;
    }

//    @Inject(method = "findCrosshairTarget",
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;",
//                    shift = At.Shift.BEFORE))
//    private void UpdateFocusedEntity(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> cir,
//                                     @Local(ordinal = 0)/*vec3d*/ Vec3d posVec, @Local(ordinal = 1)/*vec3d2*/ Vec3d rotVec){
//        if (SynchroClient.applyNewHud){
//            double squaredVisibleDistance;
//            if (this.client instanceof FocusingProvider thisClient){
//                thisClient.synchro$setFocusingResult(camera.raycast(MAX_DISTANCE, tickDelta, false));
//                if (thisClient.synchro$getFocusingResult() != null) {
//                    squaredVisibleDistance = thisClient.synchro$getFocusingResult().getPos().squaredDistanceTo(posVec);
//                }
//                else squaredVisibleDistance = MAX_DISTANCE * MAX_DISTANCE;
//                Vec3d maxVec = posVec.multiply(MAX_DISTANCE);
//                Box box = camera.getBoundingBox().stretch(maxVec).expand(1.0, 1.0, 1.0);
//                EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, posVec, posVec.add(maxVec), box, entity -> !entity.isSpectator() && entity.canHit(), squaredVisibleDistance);
//                if (entityHitResult != null) {
//                    thisClient.synchro$setFocusedEntity(entityHitResult.getEntity());
//                }
//                else thisClient.synchro$setFocusedEntity(null);
//            }
//        }
//    }
}
