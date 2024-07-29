package name.synchro.guidance;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuidanceMainScreen extends GuidanceScreen {
    private static final int SIDEBAR_WIDTH = 80;
    protected static final ButtonTextures DEBUG_TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));

    public GuidanceMainScreen(Text title) {
        super(title);
    }

    public GuidanceMainScreen() {
        this(Text.translatable("title.synchro.guidance_screen"));
    }

    @Override
    protected void init() {
        super.init();
        titleX = (SIDEBAR_WIDTH - textRenderer.getWidth(title)) / 2;
        titleY = topY + 10;
        addDrawableChild(debugScreenButton());
        addDrawableChild(configScreenButton());

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(leftX, topY, leftX + SIDEBAR_WIDTH, bottomY, 0x3f000000);
        super.render(context, mouseX, mouseY, delta);
    }

    private TexturedButtonWidget debugScreenButton(){
        int height = 20;
        int width = 20;
        assert client != null;
        TexturedButtonWidget buttonWidget = new TexturedButtonWidget(rightX - width, bottomY - height, width, height, DEBUG_TEXTURES, button -> client.setScreen(new DebugScreen(this)));
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.synchro.debug_screen_button")));
        return buttonWidget;
    }

    private TexturedButtonWidget configScreenButton(){
        int height = 20;
        int width = 20;
        assert client != null;
        TexturedButtonWidget buttonWidget = new TexturedButtonWidget(leftX, bottomY - height, width, height, DEBUG_TEXTURES, button -> client.setScreen(new ConfigScreen(this)));
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.synchro.config_button")));
        return buttonWidget;
    }
}
