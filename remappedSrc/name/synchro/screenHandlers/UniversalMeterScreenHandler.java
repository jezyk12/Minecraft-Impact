package name.synchro.screenHandlers;

import name.synchro.registrations.RegisterScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class UniversalMeterScreenHandler extends ScreenHandler {
    public int testValue;
    public UniversalMeterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, 114514);
    }
    public UniversalMeterScreenHandler(int syncId, int testValue){
        super(RegisterScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER,syncId);
        this.testValue = testValue;
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
