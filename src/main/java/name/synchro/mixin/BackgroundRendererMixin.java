package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.mixinHelper.CameraDuck;
import name.synchro.mixinHelper.CameraGasSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;
    @Shadow
    private static long lastWaterFogColorUpdateTime;

    @Inject(method = "applyFog",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private static void applyFogMixin(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Local BackgroundRenderer.FogData fogData){
        CameraGasSubmersionType cameraGasSubmersionType = ((CameraDuck)camera).getGasSubmersionType();
        if (cameraGasSubmersionType!=CameraGasSubmersionType.NONE) {
            fogData.fogStart = -8.0f;
            fogData.fogEnd = 16.0f;
            fogData.fogShape = FogShape.SPHERE;
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getPos()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 3,
                    shift = At.Shift.BEFORE))
    private static void renderMixin(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci){
        CameraGasSubmersionType cameraGasSubmersionType = ((CameraDuck)camera).getGasSubmersionType();
        if (cameraGasSubmersionType!=CameraGasSubmersionType.NONE){
            red = 0.5f;
            green = 0.5f;
            blue = 0.5f;
            lastWaterFogColorUpdateTime = -1L;
        }
    }
}
