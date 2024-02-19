package name.synchro.blockEntities;

import name.synchro.electricNetwork.AbstractConsumerBlockEntity;
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

import static name.synchro.registrations.RegisterBlockEntities.ELECTRIC_LAMP_BLOCK_ENTITY;

public class ElectricLampBlockEntity extends AbstractConsumerBlockEntity implements NamedScreenHandlerFactory {
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
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
    public ElectricLampBlockEntity(BlockPos pos, BlockState state) {
        super(ELECTRIC_LAMP_BLOCK_ENTITY, pos, state, new ElectricCapacity(0.05f),500000f,2000.0f);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("title.synchro.electric_lamp");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new UniversalMeterScreenHandler(syncId, 1919810,propertyDelegate);
    }
}
