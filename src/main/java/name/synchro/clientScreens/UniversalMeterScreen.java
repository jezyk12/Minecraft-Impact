package name.synchro.clientScreens;

import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.Synchro;
import name.synchro.electricNetwork.DisplayConvert;
import name.synchro.screenHandlers.UniversalMeterScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UniversalMeterScreen extends HandledScreen<UniversalMeterScreenHandler> {
    public CyclingButtonWidget<MeterMode> meterModeButton;
    public enum MeterMode{VOLTAGE, CURRENT, POWER}
    public MeterMode mode = MeterMode.VOLTAGE;
    private static final Identifier TEXTURE =
            new Identifier(Synchro.MOD_ID, "textures/gui/meter_gui.png");
    private static DisplayConvert.PhysicalQuantities modeOfType(MeterMode mode){
        switch (mode){
            case VOLTAGE -> {return DisplayConvert.PhysicalQuantities.VOLTAGE;}
            case CURRENT -> {return DisplayConvert.PhysicalQuantities.CURRENT;}
            case POWER -> {return DisplayConvert.PhysicalQuantities.POWER;}
            default -> {return DisplayConvert.PhysicalQuantities.UNKNOWN;}
        }
    }private static String modeOfKey(MeterMode mode){
        switch (mode){
            case VOLTAGE -> {return "name.synchro.voltage";}
            case CURRENT -> {return "name.synchro.current";}
            case POWER -> {return "name.synchro.power";}
            default -> {return "???";}
        }
    }
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
        String displayStr = DisplayConvert.toDisplayStr(modeOfType(mode), Float.intBitsToFloat(getScreenHandler().getPhysicalValue(modeOfType(mode))));
        textRenderer.draw(matrices, displayStr,
                (width - textRenderer.getWidth(displayStr))/2f,(height-backgroundHeight)/2f+32f-textRenderer.fontHeight/2f,0xffffff);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    private static Text modeToText(MeterMode mode){
        return Text.translatable(modeOfKey(mode));
    }
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        meterModeButton = CyclingButtonWidget.builder(UniversalMeterScreen::modeToText).values(MeterMode.values())
                .initially(MeterMode.VOLTAGE).omitKeyText().build(width/2-32,height/2+backgroundHeight/10,64,20,Text.of("Mode"),
                        ((button, value) -> {mode = value;}));
        addDrawableChild(meterModeButton);
    }
}
