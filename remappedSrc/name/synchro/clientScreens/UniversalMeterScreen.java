package name.synchro.clientScreens;

import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.Synchro;
import name.synchro.screenHandlers.UniversalMeterScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UniversalMeterScreen extends HandledScreen<UniversalMeterScreenHandler> {
    public ButtonWidget buttonWidget;
    private static final Identifier TEXTURE =
            new Identifier(Synchro.MOD_ID, "textures/gui/meter_gui.png");
    public UniversalMeterScreen(UniversalMeterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 96;
        this.backgroundHeight = 112;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        //this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
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
        buttonWidget = ButtonWidget.builder(Text.of("BUTTON!"), button -> {remove(buttonWidget);})
                .dimensions(48,96,40,20).build();
        addDrawableChild(buttonWidget);
    }
}
