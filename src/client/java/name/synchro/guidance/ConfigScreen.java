package name.synchro.guidance;

import name.synchro.SynchroClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends GuidanceScreen{
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    protected ConfigScreen(Text title, Screen parent) {
        super(title, parent);
    }

    public ConfigScreen(Screen parent) {
        this(Text.translatable("title.synchro.config_screen"), parent);
    }

    @Override
    protected void init() {
        super.init();
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().marginX(10).marginBottom(5).alignHorizontalCenter().alignHorizontalCenter();
        GridWidget.Adder adder = grid.createAdder(2);
        adder.add(applyNewHudButton());
        adder.add(displayExtraCollisionsButton());
        adder.add(ButtonWidget.builder(Text.translatable("config.synchro.done"), button -> {this.close();}).build());
        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, topY + 50, this.width, this.height, 0.5f, 0.0f);
        grid.forEachChild(this::addDrawableChild);
    }

    private CyclingButtonWidget<Boolean> applyNewHudButton(){
        CyclingButtonWidget<Boolean> buttonWidget = CyclingButtonWidget.onOffBuilder().build(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable("config.synchro.apply_new_hud"), (button, value) -> SynchroClient.applyNewHud = value);
        buttonWidget.setValue(SynchroClient.applyNewHud);
        return buttonWidget;
    }

    private CyclingButtonWidget<Boolean> displayExtraCollisionsButton(){
        CyclingButtonWidget<Boolean> buttonWidget = CyclingButtonWidget.onOffBuilder().build(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable("config.synchro.display_extra_collisions"), (button, value) -> SynchroClient.displayExtraCollisions = value);
        buttonWidget.setValue(SynchroClient.displayExtraCollisions);
        return buttonWidget;
    }
}
