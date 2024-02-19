package name.synchro.guidance;

import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.SynchroClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuidanceScreen extends HandledScreen<GuidanceScreenHandler> {
    public CyclingButtonWidget<Boolean> newHudButton;
    public GuidanceScreen(GuidanceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0xffffff);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        newHudButton = CyclingButtonWidget.onOffBuilder(SynchroClient.applyNewHud).build(width / 2 - 50, height / 2, 100, 20, Text.of("Apply new HUD"),
                (button, value) -> SynchroClient.applyNewHud = value);
        addDrawableChild(newHudButton);
    }
}
