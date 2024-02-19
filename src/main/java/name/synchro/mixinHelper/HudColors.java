package name.synchro.mixinHelper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.hud.InGameHud;

import java.util.Map;

public enum HudColors {
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
    HUNGER_BAR_EXHAUSTION(0x3f000000);

    public static final Map<InGameHud.HeartType, HudColors> HEART_TYPE_COLOR_MAP =
            Maps.newHashMap(ImmutableMap.of(
                    InGameHud.HeartType.NORMAL, HEALTH_BAR_NORMAL,
                    InGameHud.HeartType.POISONED, HEALTH_BAR_POISONED,
                    InGameHud.HeartType.WITHERED, HEALTH_BAR_WITHERED,
                    InGameHud.HeartType.ABSORBING, HEALTH_BAR_ABSORBING,
                    InGameHud.HeartType.FROZEN, HEALTH_BAR_FROZEN));
    public final int color;
    HudColors(int color) {
        this.color = color;
    }
}
