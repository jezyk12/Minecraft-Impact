package name.synchro.guidance;

import name.synchro.SynchroClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class DebugScreen extends GuidanceScreen {
    private static final int WIDGET_HEIGHT = 20;
    private static final int WIDGET_WIDTH = 100;

    public ArrayList<TextFieldWidget> textFieldWidgets;
    protected DebugScreen(Text title) {
        super(title);
    }

    public DebugScreen(Screen parent){
        this(Text.translatable("title.synchro.debug_screen"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        this.textFieldWidgets = new ArrayList<>();
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().marginX(5).marginBottom(10).alignHorizontalCenter().alignHorizontalCenter();
        GridWidget.Adder adder = grid.createAdder(4);
        adder.add(createButton(Text.of("Button 1"), button -> {
        }));
        adder.add(createButton(Text.of("Button 2"), button -> {
        }));
        adder.add(createButton(Text.of("Button 3"), button -> {
        }));
        adder.add(createButton(Text.of("Button 4"), button -> {
        }));
        adder.add(createTextFieldWidget("debug number 0"));
        adder.add(createTextFieldWidget("debug number 1"));
        adder.add(createTextFieldWidget("debug number 2"));
        adder.add(createTextFieldWidget("debug number 3"));
        adder.add(createTextFieldWidget("debug number 4"));
        adder.add(createTextFieldWidget("debug number 5"));
        adder.add(createTextFieldWidget("debug number 6"));
        adder.add(createTextFieldWidget("debug number 7"));
        adder.add(createButton(Text.of("Save"), button -> {
            saveDebugData(false);
        }));
        adder.add(createButton(Text.of("Save and Exit"), button -> {
            saveDebugData(true);
        }));
        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, topY + 50, this.width, this.height, 0.5f, 0.0f);
        grid.forEachChild(this::addDrawableChild);
    }

    private ButtonWidget createButton(Text message, ButtonWidget.PressAction onPress) {
        return ButtonWidget.builder(message, onPress).size(WIDGET_WIDTH, WIDGET_HEIGHT).build();
    }

    private TextFieldWidget createTextFieldWidget(String name) {
        TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, 0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Text.of(name));
        textFieldWidget.setMaxLength(16);
        textFieldWidget.setText(String.valueOf(SynchroClient.debugNumbers.get(textFieldWidgets.size())));
        this.textFieldWidgets.add(textFieldWidget);
        return textFieldWidget;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        int size = textFieldWidgets.size();
        String[] temp = new String[size];
        for (int i = 0; i < size; ++i){
            temp[i] = textFieldWidgets.get(i).getText();
        }
        init(client, width, height);
        for (int i = 0; i < size; ++i){
            textFieldWidgets.get(i).setText(temp[i]);
        }
    }

    private void saveDebugData(boolean exit){
        for (int i = 0; i < textFieldWidgets.size(); ++i) {
            SynchroClient.debugNumbers.set(i, parseFloat(textFieldWidgets.get(i).getText()));
        }
        if (exit) {
            assert client != null;
            client.setScreen(null);
        }
    }

    private static float parseFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException numberFormatException) {
            return 0f;
        }
    }
}
