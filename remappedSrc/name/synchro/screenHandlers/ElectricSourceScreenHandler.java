package name.synchro.screenHandlers;

import name.synchro.registrations.RegisterScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class ElectricSourceScreenHandler extends ScreenHandler {
    public int testValue;
    public ElectricSourceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, 114514);
    }
    public ElectricSourceScreenHandler(int syncId, PlayerInventory playerInventory, int testValue){
        super(RegisterScreenHandlers.ELECTRIC_SOURCE_SCREEN_HANDLER,syncId);
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
