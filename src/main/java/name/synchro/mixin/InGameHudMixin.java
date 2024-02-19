package name.synchro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.SynchroClient;
import name.synchro.mixinHelper.HudColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.GUI_ICONS_TEXTURE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique private static final int L_BAR = 80;
    @Unique private static final int L_OUT = 82;
    @Unique private static final int H_BAR = 7;
    @Unique private static final int H_OUT = 9;
    @Unique private static final int OX = 8;

    @Unique float lastHealthFloat;
    @Unique float changingHealthFloat;
    @Unique long lastTime;
    @Unique boolean damaging = false;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow public abstract void drawHeart(MatrixStack matrices, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart);

    @Shadow private int ticks;

    @Inject(method = "renderHealthBar",
            at=@At("HEAD"),
            cancellable = true)
    private void renderHealthBarMixin(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SynchroClient.applyNewHud) {
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
            InGameHud.fill(matrices, x, y, absorptionLength > 0 ? absorptionLength < L_BAR ? x+absorptionLength : x+L_BAR : x, y+H_BAR, HudColors.HEALTH_BAR_ABSORBING.color);
            String extraDisplay = absorptionHealth > 0 ? " + %.1f".formatted(absorptionHealth) : " / %.1f".formatted(playerMaxHealth);
            String displayHealthValues = "%.1f".formatted(playerHealth) + extraDisplay;
            InGameHud.drawCenteredTextWithShadow(matrices, this.getTextRenderer(), Text.of(displayHealthValues), x+L_BAR/2, y, absorptionLength <= 0 ? 0xffffff : 0xcfcf00);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
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
            if (armorValue > 0){
                InGameHud.drawTexture(matrices, x, y, 34, 9, 9, 9);
                InGameHud.drawTextWithShadow(matrices, getTextRenderer(), Text.of("+" + armorValue), x + 10, y, 0xffffff);
                RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
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
            float saturation = hungerManager.getSaturationLevel();
            float exhaustion = hungerManager.getExhaustion();
            boolean haveSaturation = true;
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
            String displayHungerValues = "%.1f".formatted(foodLevel - (haveSaturation || foodLevel < 1 ? 0f : exhaustion/4f))
                    + (haveSaturation ? " + %.1f".formatted(displaySaturation > 0 ? displaySaturation : 0f ) : " / 20.0");
            InGameHud.drawCenteredTextWithShadow(matrices, getTextRenderer(), Text.of(displayHungerValues),x-L_BAR/2,y, haveSaturation ? 0xcfcf00 : 0xffffff);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        }
    }


}
