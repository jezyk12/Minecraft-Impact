package name.synchro.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import name.synchro.playNetworking.FireTicksDataPayload;
import name.synchro.playNetworking.HungerDataPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Map;

public final class NewHudUtil {
    public static void receiveHungerDataPacket(HungerDataPayload payload, ClientPlayNetworking.Context context) {
        float saturation = payload.saturation();
        float exhaustion = payload.exhaustion();
        context.client().execute(() -> {
            ClientPlayerEntity clientPlayer = context.client().player;
            if (clientPlayer != null) {
                clientPlayer.getHungerManager().setSaturationLevel(saturation);
                clientPlayer.getHungerManager().setExhaustion(exhaustion);
            }
        });
    }

    public static void receiveFireTicksDataPacket(FireTicksDataPayload payload, ClientPlayNetworking.Context context) {
        int fireTicks = payload.fireTicks();
        context.client().execute(() -> {
            ClientPlayerEntity clientPlayer = context.client().player;
            if (clientPlayer instanceof PlayerFireTickSync) {
                ((PlayerFireTickSync) clientPlayer).synchro$setTheFireTicks(fireTicks);
            }
        });
    }

    public enum Colors {
        HEALTH_BAR_OUTLINE(0xff3f0000),
        HEALTH_BAR_DAMAGING(0xcffadbd8),
        HEALTH_BAR_NORMAL(0xffbf0000),
        HEALTH_BAR_POISONED(0xff003f00),
        HEALTH_BAR_WITHERED(0xff0f0f0f),
        HEALTH_BAR_ABSORBING(0xbfe4d03f),
        HEALTH_BAR_FROZEN(0xff85c1e9),
        HUNGER_BAR_OUTLINE(0xff412A20),
        HUNGER_BAR_NORMAL(0xffb88458),
        HUNGER_BAR_SATURATION(0x7fD4AF37),
        HUNGER_BAR_HUNGER(0xff748458),
        HUNGER_BAR_EXHAUSTION(0x3f000000),
        AIR_BAR_OUTLINE(0x1f0094FF),
        AIR_BAR_DROWNING(0x7f7f0000),
        AIR_BAR_NORMAL(0xff0094FF);

        public static final Map<InGameHud.HeartType, Colors> HEART_TYPE_COLOR_MAP =
                Maps.newHashMap(ImmutableMap.of(
                        InGameHud.HeartType.NORMAL, HEALTH_BAR_NORMAL,
                        InGameHud.HeartType.POISONED, HEALTH_BAR_POISONED,
                        InGameHud.HeartType.WITHERED, HEALTH_BAR_WITHERED,
                        InGameHud.HeartType.ABSORBING, HEALTH_BAR_ABSORBING,
                        InGameHud.HeartType.FROZEN, HEALTH_BAR_FROZEN));
        public final int color;
        Colors(int color) {
            this.color = color;
        }
    }
}
