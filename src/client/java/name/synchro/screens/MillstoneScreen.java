package name.synchro.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.Synchro;
import name.synchro.screenHandlers.MillstoneScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Math;
import org.joml.Quaternionf;

import static name.synchro.blockEntities.MillstoneBlockEntity.*;

public class MillstoneScreen extends HandledScreen<MillstoneScreenHandler> {
    private static final Identifier GUI = Identifier.of(Synchro.MOD_ID, "textures/gui/millstone_gui.png");
    private int guiX, guiY;
    private int lastSpeedDegree = -114514;

    public MillstoneScreen(MillstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiX = (width - backgroundWidth) / 2;
        guiY = (height - backgroundHeight) / 2;
        context.drawTexture(GUI, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawArrow(context, getScreenHandler().getInteger(INT_PROGRESS));
        drawPointer(context, getScreenHandler().getInteger(INT_SPEED));
        drawFeedOutline(context);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void drawPointer(DrawContext context, int speed){
        if (lastSpeedDegree == -114514) lastSpeedDegree = Math.abs(speed) * 60;
        int d = Integer.compare(Math.abs(speed) * 60, lastSpeedDegree);
        RenderSystem.setShaderTexture(0, GUI);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(guiX + 31.5, guiY + 65.5, 0);
        matrices.multiply(new Quaternionf().rotateZ(Math.toRadians(lastSpeedDegree + d + 135)));
        context.drawTexture(GUI,-2, -2, 25, 167, 11, 11, 256, 256);
        matrices.pop();
        lastSpeedDegree += d;
    }

    private void drawArrow(DrawContext context, int progress){
        RenderSystem.setShaderTexture(0, GUI);
        context.drawTexture(GUI, guiX + 97, guiY + 35, 0, 167, progress, 17, 256, 256);
    }

    private void drawFeedOutline(DrawContext context){
        if (getScreenHandler().getSlot(SLOT_FEED).getStack().isEmpty()){
            RenderSystem.setShaderTexture(0, GUI);
            context.drawTexture(GUI, guiX + 24, guiY + 20, 38, 168, 18, 18, 256, 256);
        }
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
