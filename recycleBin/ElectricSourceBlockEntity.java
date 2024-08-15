package name.synchro.blockEntities;

import name.synchro.electricNetwork.AbstractSourceBlockEntity;
import name.synchro.electricNetwork.ElectricCapacity;
import name.synchro.screenHandlers.UniversalMeterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static name.synchro.registrations.ModBlockEntities.ELECTRIC_SOURCE_BLOCK_ENTITY;

public class ElectricSourceBlockEntity extends AbstractSourceBlockEntity implements NamedScreenHandlerFactory {
    public PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index){
                case 0 ->{
                    return Float.floatToIntBits(DATA.voltage);
                }
                case 1 ->{
                    return Float.floatToIntBits(DATA.current);
                }
                case 2 ->{
                    return Float.floatToIntBits(DATA.power);
                }
                default -> {
                    return 0;
                }
            }
        }

        @Override
        public void set(int index, int value) {}
        @Override
        public int size() {
            return 3;
        }
    };
    public ElectricSourceBlockEntity(BlockPos pos, BlockState state) {
        super(ELECTRIC_SOURCE_BLOCK_ENTITY, pos, state, new ElectricCapacity(0.05f),500000f,1000);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("title.synchro.electric_source");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new UniversalMeterScreenHandler(syncId, 1919810,propertyDelegate);
    }
}
