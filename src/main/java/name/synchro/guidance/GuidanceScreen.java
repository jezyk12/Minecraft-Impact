package name.synchro.guidance;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class GuidanceScreen extends Screen {
    public int leftX;
    public int middleX;
    public int rightX;
    public int topY;
    public int middleY;
    public int bottomY;
    protected float titleX;
    protected float titleY;
    public Screen parent;

    protected GuidanceScreen(Text title) {
        super(title);
    }

    protected GuidanceScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.leftX = 0;
        this.middleX = width / 2;
        this.rightX = width;
        this.topY = 0;
        this.middleY = height / 2;
        this.bottomY = height;
        this.titleX = middleX - (float) textRenderer.getWidth(title) / 2;
        this.titleY = topY + 20;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        renderForeground(matrices, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);

    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    protected void renderForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, this.titleX, this.titleY, 0xffffff);
    }

}
