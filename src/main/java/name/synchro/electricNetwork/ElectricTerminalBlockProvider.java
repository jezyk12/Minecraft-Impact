package name.synchro.electricNetwork;

import name.synchro.networkLink.networkBlockAPI.NetworkSearchProvider;
import net.minecraft.block.BlockEntityProvider;

public interface ElectricTerminalBlockProvider extends NetworkSearchProvider, BlockEntityProvider {
    @Override
    default boolean isNotDeadEnd(){
        return false;
    }
}
