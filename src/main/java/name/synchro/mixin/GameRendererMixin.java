package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.SynchroClient;
import name.synchro.mixinHelper.FocusingProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Final
    @Shadow MinecraftClient client;

    @Unique private static final double MAX_DISTANCE = 64.0;


    @Inject(method = "updateTargetedEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;",
                    shift = At.Shift.BEFORE))
    private void UpdateFocusedEntity(float tickDelta, CallbackInfo ci,
                                     @Local/*entity2*/ Entity player,
                                     @Local(ordinal = 0)/*vec3d*/ Vec3d camaraPosVec,
                                     @Local(ordinal = 1)/*vec3d2*/ Vec3d rotationVec){
        if (SynchroClient.applyNewHud){
            double squaredVisibleDistance;
            if (this.client instanceof FocusingProvider thisClient){
                thisClient.synchro$setFocusingResult(player.raycast(MAX_DISTANCE, tickDelta, false));
                if (thisClient.synchro$getFocusingResult() != null) {
                    squaredVisibleDistance = thisClient.synchro$getFocusingResult().getPos().squaredDistanceTo(camaraPosVec);
                }
                else squaredVisibleDistance = MAX_DISTANCE * MAX_DISTANCE;
                Box box = player.getBoundingBox().stretch(rotationVec.multiply(MAX_DISTANCE)).expand(1.0, 1.0, 1.0);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(player, camaraPosVec, camaraPosVec.add(rotationVec.multiply(MAX_DISTANCE)), box, entity -> !entity.isSpectator() && entity.canHit(), squaredVisibleDistance);
                if (entityHitResult != null) {
                    thisClient.synchro$setFocusedEntity(entityHitResult.getEntity());
                }
                else thisClient.synchro$setFocusedEntity(null);
            }
        }
    }
}
