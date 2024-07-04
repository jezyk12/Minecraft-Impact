package name.synchro.screenHandlers;

import name.synchro.electricNetwork.DisplayConvert;
import name.synchro.registrations.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

public class UniversalMeterScreenHandler extends ScreenHandler {
    public int testValue;
    public PropertyDelegate propertyDelegate;
    public UniversalMeterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, 114514,new ArrayPropertyDelegate(3));
    }
    public UniversalMeterScreenHandler(int syncId, int testValue, PropertyDelegate propertyDelegate){
        super(ModScreenHandlers.UNIVERSAL_METER_SCREEN_HANDLER,syncId);
        this.testValue = testValue;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    public int getPhysicalValue(DisplayConvert.PhysicalQuantities type){
        switch (type){
            case VOLTAGE -> {
                return propertyDelegate.get(0);
            }
            case CURRENT -> {
                return propertyDelegate.get(1);
            }
            case POWER -> {
                return propertyDelegate.get(2);
            }
            default -> {
                return 0;
            }
        }
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
