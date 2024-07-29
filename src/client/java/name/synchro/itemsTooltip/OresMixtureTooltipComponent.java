package name.synchro.itemsTooltip;

import name.synchro.items.OresMixture;
import name.synchro.util.Metals;
import name.synchro.util.MetalsComponentsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record OresMixtureTooltipComponent
        (List<Pair<Integer, Integer>> sortedContents, int rockContent)
        implements TooltipComponentExtra {

    public static TooltipComponent getComponent(TooltipData tooltipData){
        if (tooltipData instanceof OresMixture.TooltipDataRecord tooltipDataRecord){
            Map<Integer, Integer> contents = tooltipDataRecord.contents();
            List<Pair<Integer, Integer>> pairs = new ArrayList<>();
            contents.forEach((k, v) -> pairs.add(new Pair<>(k, v)));
            pairs.sort((a, b) -> b.getRight() - a.getRight());
            return new OresMixtureTooltipComponent(pairs,
                    MetalsComponentsHelper.getRockContent(contents, tooltipDataRecord.drpTotal()));
        }
        return TooltipComponent.of(OrderedText.EMPTY);
    }

    @Override
    public int getHeight() {
        if (creative()) {
            return (sortedContents().size() + 2) * 9;
        }
        return 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        if (creative()) {
            String nameMetal = Text.translatable("tooltip.synchro.metal").getString();
            return textRenderer.getWidth(Text.of("■ " + nameMetal + "00 00000drp"));
        }
        return 0;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        TooltipComponentExtra.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
        if (creative() && client().world != null) {
            textRenderer.draw(Text.translatable("tooltip.synchro.metal_content"), x, y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            Metals metals = ((Metals.Provider) client().world).getMetals();
            String nameMetal = Text.translatable("tooltip.synchro.metal").getString();
            for (int i = 0; i < sortedContents().size(); i++) {
                Integer numId = sortedContents().get(i).getLeft();
                Integer content = sortedContents().get(i).getRight();
                Text line = Text.of("■ " + nameMetal + (numId > 9 ? numId : "0" + numId) + " " + content + "drp");
                int color = metals.getVariants().get(numId).color();
                textRenderer.draw(line, x, y + 9 * (i + 1), color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            }
            Text lineOfRock = Text.of("■ " + Text.translatable("tooltip.synchro.rock").getString() + " " + rockContent() + "drp");
            textRenderer.draw(lineOfRock, x, y + 9 * (sortedContents().size() + 1), 0xd8d8d8, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        }
    }

}
