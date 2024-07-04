package name.synchro.screenHandlers;

import name.synchro.registrations.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static name.synchro.blockEntities.MillstoneBlockEntity.*;


public class MillstoneScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.MILLSTONE_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        if (inventory.size() == INV_SIZE){
            inventory.onOpen(playerInventory.player);
            addAllSlots(playerInventory, inventory);
        }
    }

    //Client Constructor
    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INV_SIZE), new ArrayPropertyDelegate(INTEGERS_SIZE));
    }

    private void addAllSlots(PlayerInventory playerInventory, Inventory inventory) {
        int m;
        int l;
        this.addSlot(new Slot(inventory, SLOT_INPUT, 73, 35){
            @Override
            public boolean canInsert(ItemStack stack) {
                return canInsertForProcessing(playerInventory.player.world, stack);
            }
        });
        this.addSlot(new Slot(inventory, SLOT_OUTPUT, 129, 35){
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(inventory, SLOT_FEED, 24, 20){
            @Override
            public boolean canInsert(ItemStack stack) {
                return MILLSTONE_FEEDS.containsKey(stack.getItem());
            }
        });
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotId) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (slotId < INV_SIZE) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public int getInteger(int index){
        return this.propertyDelegate.get(index);
    }
}
