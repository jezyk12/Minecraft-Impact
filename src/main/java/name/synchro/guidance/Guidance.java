package name.synchro.guidance;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class Guidance implements NamedScreenHandlerFactory {
    @Override
    public Text getDisplayName() {
        return Text.of("Guidance Screen");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GuidanceScreenHandler(syncId, playerInventory);
    }
}
