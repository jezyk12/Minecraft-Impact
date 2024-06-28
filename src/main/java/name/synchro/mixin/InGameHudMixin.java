package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.SynchroClient;
import name.synchro.mixinHelper.CameraInGas;
import name.synchro.mixinHelper.HudColors;
import name.synchro.mixinHelper.MinecraftClientDuck;
import name.synchro.mixinHelper.PlayerFireTickSync;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.client.gui.DrawableHelper.GUI_ICONS_TEXTURE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique private static final int L_BAR = 80;
    @Unique private static final int L_OUT = 82;
    @Unique private static final int H_BAR = 7;
    @Unique private static final int H_OUT = 9;
    @Unique private static final int OX = 8;

    @Unique private final int MIDDLE_X = this.scaledWidth / 2;
    @Unique private final int MIDDLE_Y = this.scaledHeight / 2;

    @Unique private float lastHealthFloat;
    @Unique private float changingHealthFloat;
    @Unique private long lastTime;
    @Unique private boolean damaging = false;
    @Unique private String displayHealth;
    @Unique private String displayHunger;
    @Unique private String displayArmor;
    @Unique private String displayAir;
    @Unique private boolean haveSaturation;
    @Unique private boolean haveAbsorption;
    @Unique private boolean haveArmor;
    @Unique private boolean lackingAir;
    @Unique private int iconsTakePlace;
    @Unique private int iconTextsTakePlace;
    @Unique @Nullable private Entity focusingEntity;
    @Unique private int beginFocusingTick = -114514;
    @Unique private float focusedEntityHealth;

    @Shadow public abstract TextRenderer getTextRenderer();
    @Shadow public abstract void drawHeart(MatrixStack matrices, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart);

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private static int WHITE;

    @Shadow public abstract int getTicks();

    @Shadow @Final private static Identifier WIDGETS_TEXTURE;

    @Inject(method = "renderHealthBar",
            at=@At("HEAD"),
            cancellable = true)
    private void renderHealthBarMixin(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SynchroClient.applyNewHud) {
            x = scaledWidth / 2 - 91;
            y = scaledHeight - 39;
            InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(player);
            int color = HudColors.HEART_TYPE_COLOR_MAP.get(heartType).color;
            float playerHealth = player.getHealth();
            float absorptionHealth = player.getAbsorptionAmount();
            float playerMaxHealth = player.getMaxHealth();

            long now = Util.getMeasuringTimeMs();
            if (playerHealth - this.changingHealthFloat < -0.001f) {
                if (!damaging) this.lastHealthFloat = this.changingHealthFloat;
                this.lastTime = now;
                this.damaging = true;
            }
            if (damaging && now - lastTime > 1000L) {
                this.lastHealthFloat = playerHealth;
                this.lastTime = now;
                this.damaging = false;

            }
            this.changingHealthFloat = playerHealth;
            int healthBarLength = MathHelper.ceil(playerHealth / playerMaxHealth * L_BAR);
            int lastHealthLength = MathHelper.ceil(this.lastHealthFloat / playerMaxHealth * L_BAR);
            int absorptionLength = MathHelper.ceil(absorptionHealth / playerMaxHealth * L_BAR);
            x -= 1;
            this.drawHeart(matrices, InGameHud.HeartType.CONTAINER, x, y, 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0), blinking, false);
            this.drawHeart(matrices, absorptionLength <= 0 ? heartType : InGameHud.HeartType.ABSORBING, x, y, 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0), blinking, false);
            x += OX + 1;
            InGameHud.fill(matrices, x, y, x+L_OUT, y+H_OUT, HudColors.HEALTH_BAR_OUTLINE.color);
            x += 1;
            y += 1;
            if (damaging) InGameHud.fill(matrices, x, y, x+lastHealthLength, y+H_BAR, HudColors.HEALTH_BAR_DAMAGING.color);
            InGameHud.fill(matrices, x, y, healthBarLength > 0 ? x + healthBarLength : x, y+H_BAR, color);
            haveAbsorption = absorptionLength > 0;
            InGameHud.fill(matrices, x, y, haveAbsorption ? absorptionLength < L_BAR ? x+absorptionLength : x+L_BAR : x, y+H_BAR, HudColors.HEALTH_BAR_ABSORBING.color);
            String extraDisplay = haveAbsorption ? " + %.1f".formatted(absorptionHealth) : " / %.1f".formatted(playerMaxHealth);
            this.displayHealth = "%.1f".formatted(playerHealth) + extraDisplay;
            ci.cancel();
        }
    }

    @ModifyConstant(method = "renderStatusBars",
    constant = @Constant(intValue = 10, ordinal = 4))
    private int skipOriginalArmorDrawing(int constant){
        if (SynchroClient.applyNewHud){
            return 0;
        }
        else return constant;
    }

    @Inject(method = "renderStatusBars",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void drawNewArmorBar(MatrixStack matrices, CallbackInfo ci,
                                 @Local(ordinal = 0) PlayerEntity playerEntity,
                                 @Local(ordinal = 3)/*m*/ int x,
                                 @Local(ordinal = 9)/*s*/ int y,
                                 @Local(ordinal = 11)/*u*/ int armorValue){
        if (SynchroClient.applyNewHud){
            x = scaledWidth / 2 - 92;
            y = scaledHeight - 49;
            if (armorValue > 0){
                RenderSystem.setShaderTexture(0, SynchroClient.MOD_ICONS);
                drawStatusIcon(matrices, x, y,18,0);
                RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
                this.displayArmor = "+" + armorValue;
                this.haveArmor = true;
            }
        }
    }

    @ModifyConstant(method = "renderStatusBars",
            constant = @Constant(intValue = 10, ordinal = 5))
    private int skipOriginalHungerDrawing(int constant){
        if (SynchroClient.applyNewHud){
            return 0;
        }
        else return constant;
    }

    @Inject(method = "renderStatusBars",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER))
    private void drawNewHungerBar(MatrixStack matrices, CallbackInfo ci,
                                  @Local(ordinal = 0) PlayerEntity playerEntity,
                                  @Local(ordinal = 0) HungerManager hungerManager,
                                  @Local(ordinal = 2)/*k*/ int foodLevel,
                                  @Local(ordinal = 4)/*n*/ int x,
                                  @Local(ordinal = 5)/*o*/ int y){
        if (SynchroClient.applyNewHud){
            x = scaledWidth / 2 + 91;
            y = scaledHeight - 39;
            float saturation = hungerManager.getSaturationLevel();
            float exhaustion = hungerManager.getExhaustion();
            haveSaturation = true;
            boolean inHunger = playerEntity.hasStatusEffect(StatusEffects.HUNGER);
            int foodLength = foodLevel * L_BAR / 20;
            int saturationLength = MathHelper.ceil(saturation * L_BAR / 20);
            int exhaustionLength = foodLevel > 0 ? MathHelper.floor(exhaustion * L_BAR/20/4) : 0;
            x -= OX;
            InGameHud.drawTexture(matrices, x, y, inHunger ? 16+13*9 : 16, 27, 9, 9);
            InGameHud.drawTexture(matrices, x, y, inHunger ? 16+8*9 : 16+4*9, 27, 9, 9);
            InGameHud.fill(matrices, x-L_OUT, y, x, y+H_OUT, HudColors.HUNGER_BAR_OUTLINE.color);
            x -= 1;
            y += 1;
            InGameHud.fill(matrices, x-foodLength, y, x, y+H_BAR, playerEntity.hasStatusEffect(StatusEffects.HUNGER) ? HudColors.HUNGER_BAR_HUNGER.color : HudColors.HUNGER_BAR_NORMAL.color);
            if (saturationLength == 0){
                InGameHud.fill(matrices, x-foodLength, y, x-foodLength+exhaustionLength, y+H_BAR, HudColors.HUNGER_BAR_EXHAUSTION.color);
                haveSaturation = false;
            }
            else {
                InGameHud.fill(matrices, x-saturationLength, y, x, y+H_BAR, HudColors.HUNGER_BAR_SATURATION.color);
                InGameHud.fill(matrices, x-saturationLength, y, x-saturationLength+exhaustionLength, y+H_BAR, HudColors.HUNGER_BAR_EXHAUSTION.color);
            }
            float displaySaturation = saturation - exhaustion/4;
            this.displayHunger = "%.1f".formatted(foodLevel - (haveSaturation || foodLevel < 1 ? 0f : exhaustion/4f))
                    + (haveSaturation ? " + %.1f".formatted(displaySaturation > 0 ? displaySaturation : 0f ) : " / 20.0");
        }
    }

    @ModifyArg(method = {"renderStatusBars"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I",
                    ordinal = 0))
    private double skipOriginalAirDrawing0(double value/*ab*/){
        if (SynchroClient.applyNewHud){
            return 0;
        }
        else return value;
    }
    @ModifyArg(method = {"renderStatusBars"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I",
                    ordinal = 1))
    private double skipOriginalAirDrawing1(double value/*ac*/){
        if (SynchroClient.applyNewHud){
            return 0;
        }
        else return value;
    }

    @Inject(method = {"renderStatusBars"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I",
                    ordinal = 1,
                    shift = At.Shift.AFTER))
    private void drawNewAirBar(MatrixStack matrices, CallbackInfo ci,
                               @Local(ordinal = 4)/*n*/ int x,
                               @Local(ordinal = 10)/*t*/ int y,
                               @Local(ordinal = 15)/*z*/ int air,
                               @Local(ordinal = 14)/*y*/ int maxAir){
        if (SynchroClient.applyNewHud){
            x = scaledWidth / 2 + 91;
            y = scaledHeight - 49;
            int filledLength = MathHelper.floor(L_BAR * (1 - (float) air / maxAir));
            x -= OX;
            InGameHud.drawTexture(matrices, x, y, 16, 18, 9, 9);
            InGameHud.fill(matrices, x-L_OUT, y, x, y+H_OUT, HudColors.AIR_BAR_OUTLINE.color);
            if (air < 0) InGameHud.fill(matrices, x-L_OUT, y, x-L_OUT+Math.abs(air*L_OUT/20), y+H_OUT, HudColors.AIR_BAR_DROWNING.color);
            x -= 1;
            y += 1;
            InGameHud.fill(matrices, x-L_BAR, y, x-L_BAR+Math.min(filledLength, L_BAR), y+H_BAR, HudColors.AIR_BAR_NORMAL.color);
            this.displayAir = Math.max(air * 100 / maxAir, 0) + " %";
            this.lackingAir = true;
        }
    }

    @WrapOperation(method = "renderExperienceBar",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int skipOriginalExpDisplay(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color, Operation<Integer> original){
        if (SynchroClient.applyNewHud){
            return 0;
        }
        else return original.call(instance, matrices, text, x, y, color);
    }

    @Inject(method = "renderExperienceBar",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                    shift = At.Shift.BEFORE))
    private void drawNewExpDisplay(MatrixStack matrices, int x, CallbackInfo ci){
        if (SynchroClient.applyNewHud){
            int expLevel = 0;
            int expValue = 0;
            int expNext = 1145141919;
            int x0 = 0;
            int y0 = -58;
            if (this.client.player != null) {
                expLevel = this.client.player.experienceLevel;
                expNext =  this.client.player.getNextLevelExperience();
                expValue = (int) (this.client.player.experienceProgress * expNext);
            }
            String displayExp = expValue + " / " + expNext;
            matrices.push();
            float scale = 0.6f;
            y0 = (int) (y0 * 0.5 / scale);
            matrices.scale(scale, scale, 1f);
            matrices.translate((float) scaledWidth / scale / 2, (float) scaledHeight / scale, 0f);
            int w = this.getTextRenderer().getWidth(String.valueOf(expLevel));
            drawBoldText(matrices, displayExp, x0-150, y0, WHITE);
            drawBoldText(matrices, String.valueOf(expLevel), x0+150-w, y0, 8453920);
            matrices.pop();
        }
    }

    @Unique
    private void drawBoldText(MatrixStack matrices, String string, int x, int y, int color){
        this.getTextRenderer().draw(matrices, string, (float)(x + 1), (float)y, 0);
        this.getTextRenderer().draw(matrices, string, (float)(x - 1), (float)y, 0);
        this.getTextRenderer().draw(matrices, string, (float)x, (float)(y + 1), 0);
        this.getTextRenderer().draw(matrices, string, (float)x, (float)(y - 1), 0);
        this.getTextRenderer().draw(matrices, string, (float)x, (float)y, color);
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void initRenderingStatusBars(MatrixStack matrices, CallbackInfo ci){
        if (SynchroClient.applyNewHud){
            this.haveArmor = false;
            this.lackingAir = false;
            this.iconsTakePlace = 0;
            this.iconTextsTakePlace = 0;
        }
    }

    @Inject(method = "renderStatusBars",at = @At(value = "TAIL"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void endRenderingStatusBars(MatrixStack matrices, CallbackInfo ci){
        if (SynchroClient.applyNewHud){
            int x = scaledWidth / 2 - 92;
            int y = scaledHeight - 49;
            PlayerEntity player = this.getCameraPlayer();
            int fireTicks;
            if (player instanceof PlayerFireTickSync playerEntity) fireTicks = playerEntity.getTheFireTicks();
            else fireTicks = -20;
            boolean onFire = fireTicks > 0;
            String displayFire = (fireTicks > 199 ? "%.0f": "%.1f").formatted((float) fireTicks / 20) + "s";
            int frozenTicks = player.getFrozenTicks();
            boolean onFrozen = frozenTicks > 0;
            String displayFrozen = frozenTicks * 100 / 140 + "%";
            RenderSystem.setShaderTexture(0, SynchroClient.MOD_ICONS);
            if (onFire) drawStatusIcon(matrices, x, y, 0, 0);
            if (onFrozen) drawStatusIcon(matrices, x, y, 9, 0);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);

            //render texts
            TextRenderer textRenderer = this.getTextRenderer();
            matrices.push();
            matrices.scale(0.8f,0.8f,1.0f);
            matrices.translate((float) scaledWidth / 0.8 / 2, scaledHeight / 0.8, 0f);
            int x1 =  -91;
            int y1 =  -59;
            InGameHud.drawCenteredTextWithShadow(matrices, textRenderer, Text.of(displayHealth), x1 + 38, y1 + 12, haveAbsorption ? 0xcfcf00 : WHITE);
            InGameHud.drawCenteredTextWithShadow(matrices, textRenderer, Text.of(displayHunger), x1 + 143, y1 + 12, haveSaturation ? 0xcfcf00 : WHITE);
            if (lackingAir) InGameHud.drawCenteredTextWithShadow(matrices, textRenderer, Text.of(displayAir), x1 + 143, y1, WHITE );
            if (haveArmor) drawStatusIconText(matrices, textRenderer, x1, y1, Text.of(displayArmor));
            if (onFire) drawStatusIconText(matrices, textRenderer, x1, y1, Text.of(displayFire));
            if (onFrozen) drawStatusIconText(matrices, textRenderer, x1, y1, Text.of(displayFrozen));
            matrices.pop();
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        }
    }

    @Unique
    private void drawStatusIcon(MatrixStack matrices, int x, int y, int u, int v){
        x += iconsTakePlace * 29;
        InGameHud.drawTexture(matrices, x, y, u, v, 9, 9);
        InGameHud.drawTexture(matrices, x + 10, y, 0, 10, 18, 9);
        iconsTakePlace += 1;
    }

    @Unique
    private void drawStatusIconText(MatrixStack matrices, TextRenderer textRenderer, int x, int y, Text text){
        x += iconTextsTakePlace * 36;
        InGameHud.drawCenteredTextWithShadow(matrices, textRenderer, text, x, y, WHITE);
        iconTextsTakePlace += 1;
    }

    @Inject(method = "renderHotbar", at = @At(value = "TAIL"))
    private void renderFocusedEntityData(float tickDelta, MatrixStack matrices, CallbackInfo ci){
        if (SynchroClient.applyNewHud && this.client instanceof MinecraftClientDuck thisClient){
            if (thisClient.getFocusedEntity() != null){
                beginFocusingTick = this.getTicks();
                focusingEntity = thisClient.getFocusedEntity();
            }
            else if (focusingEntity != null) {
                int delay = focusingEntity.isAlive() ? 40 : 20;
                if (this.getTicks() - beginFocusingTick > delay) {
                    focusingEntity = null;
                }

            }
            if (focusingEntity != null && focusingEntity instanceof LivingEntity that) {
                String name= that.getName().getString();
                float thatHealth = that.getHealth();
                if (Math.abs(thatHealth - focusedEntityHealth)  > 0.00001){
                    focusedEntityHealth = thatHealth;
                    beginFocusingTick = this.getTicks();
                }
                String healthDisplay = "%.1f / %.1f".formatted(thatHealth, that.getMaxHealth());
                InGameHud.fill(matrices, 2, 2, 82, 36, 0xcfffffff);
                InGameHud.fill(matrices, 3, 3, 81, 35, 0xcf000000);
                RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
                this.drawHeart(matrices, InGameHud.HeartType.CONTAINER, 5, 15, 0, false, false);
                this.drawHeart(matrices, InGameHud.HeartType.NORMAL, 5, 15, 0, false, false);
                RenderSystem.setShaderTexture(0, SynchroClient.MOD_ICONS);
                InGameHud.drawTexture(matrices, 5, 25, 9, 0 ,9,9);
                getTextRenderer().draw(matrices, name, 5, 5, WHITE);
                getTextRenderer().draw(matrices, healthDisplay, 15, 15, WHITE);
                getTextRenderer().draw(matrices, that.getFrozenTicks() * 100 / 140 + " %", 15, 25, WHITE);
                RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0, shift = At.Shift.AFTER))
    private void beforeRenderingHud(MatrixStack matrices, float tickDelta, CallbackInfo ci){
        drawGasOverlay(matrices, tickDelta);
    }

    @Unique
    private void drawGasOverlay(MatrixStack matrices, float tickDelta){
        Camera camera = this.client.gameRenderer.getCamera();
        int gasSubmersionColor = CameraInGas.getGasOverlayColor(this.client.world, camera);
        if (gasSubmersionColor != -1){
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            InGameHud.fill(matrices, 0, 0, this.scaledWidth, this.scaledHeight, -90, gasSubmersionColor);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
}
