package name.synchro.guidance;

import name.synchro.registrations.RegisterScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class GuidanceScreenHandler extends ScreenHandler {
    public GuidanceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId);
    }
    public GuidanceScreenHandler(int syncId){
        super(RegisterScreenHandlers.GUIDANCE_SCREEN_HANDLER,syncId);
    }
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
