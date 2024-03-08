package name.synchro.guidance;

import name.synchro.SynchroClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GuidanceMainScreen extends GuidanceScreen {
    private static final int SIDEBAR_WIDTH = 80;

    public GuidanceMainScreen(Text title) {
        super(title);
    }

    public GuidanceMainScreen() {
        this(Text.translatable("title.synchro.guidance_screen"));
    }

    @Override
    protected void init() {
        super.init();
        titleX = (float) (SIDEBAR_WIDTH - textRenderer.getWidth(title)) / 2;
        titleY = topY + 10;
        addDrawableChild(debugScreenButton());
        addDrawableChild(configScreenButton());

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, leftX, topY, leftX + SIDEBAR_WIDTH, bottomY, 0x3f000000);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private TexturedButtonWidget debugScreenButton(){
        int height = 20;
        int width = 20;
        int u = 0;
        int v = 20;
        assert client != null;
        TexturedButtonWidget buttonWidget = new TexturedButtonWidget(rightX - width, bottomY - height, width, height, u, v, 20, SynchroClient.MOD_ICONS, button -> client.setScreen(new DebugScreen(this)));
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.synchro.debug_screen_button")));
        return buttonWidget;
    }

    private TexturedButtonWidget configScreenButton(){
        int height = 20;
        int width = 20;
        int u = 20;
        int v = 20;
        assert client != null;
        TexturedButtonWidget buttonWidget = new TexturedButtonWidget(leftX, bottomY - height, width, height, u, v, 20, SynchroClient.MOD_ICONS, button -> client.setScreen(new ConfigScreen(this)));
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.synchro.config_button")));
        return buttonWidget;
    }
}
