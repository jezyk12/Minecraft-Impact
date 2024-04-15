package name.synchro.blockEntities;

import name.synchro.electricNetwork.AbstractConsumerBlockEntity;
import name.synchro.electricNetwork.ElectricCapacity;
import name.synchro.screenHandlers.UniversalMeterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static name.synchro.registrations.RegisterBlockEntities.ELECTRIC_LAMP_BLOCK_ENTITY;

public class ElectricLampBlockEntity extends AbstractConsumerBlockEntity implements NamedScreenHandlerFactory {

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
        return new UniversalMeterScreenHandler(syncId, 1919810);
    }
}
