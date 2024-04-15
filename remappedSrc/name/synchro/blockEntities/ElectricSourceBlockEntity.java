package name.synchro.blockEntities;

import name.synchro.electricNetwork.AbstractSourceBlockEntity;
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

import static name.synchro.registrations.RegisterBlockEntities.ELECTRIC_SOURCE_BLOCK_ENTITY;

public class ElectricSourceBlockEntity extends AbstractSourceBlockEntity implements NamedScreenHandlerFactory {
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
        return new UniversalMeterScreenHandler(syncId, 1919810);
    }
}
