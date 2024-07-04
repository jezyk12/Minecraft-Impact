package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.items.CrackedOreTooltipComponent;
import name.synchro.items.OresMixture;
import name.synchro.util.MetalsComponentsHelper;
import name.synchro.util.NotSupported;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class RegisterEventsClient {
    public static void registerAll() {
        registerTooltipEvent();
        NotSupported.registerTooltip();
        Synchro.LOGGER.debug("Registered all client-side events for" + Synchro.MOD_ID);
    }

    public static void registerTooltipEvent(){
        TooltipComponentCallback.EVENT.register(tooltipData -> {
            if (tooltipData instanceof OresMixture.CrackedOreTooltipData crackedOreTooltipData){
                Map<Integer, Integer> contents = crackedOreTooltipData.contents();
                List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                contents.forEach((k, v) -> pairs.add(new Pair<>(k, v)));
                pairs.sort((a, b) -> b.getRight() - a.getRight());
                return new CrackedOreTooltipComponent(pairs, crackedOreTooltipData.advanced(), crackedOreTooltipData.creative(),
                        MetalsComponentsHelper.getRockContent(contents, crackedOreTooltipData.drpTotal()), crackedOreTooltipData.client());
            }
            return TooltipComponent.of(OrderedText.EMPTY);
        });
    }
}
