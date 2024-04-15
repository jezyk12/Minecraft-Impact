package name.synchro.electricNetwork;

import name.synchro.networkLink.networkBlockAPI.NetworkSearchProvider;

public interface ElectricConductorBlockProvider extends NetworkSearchProvider {
    @Override
    default boolean isNotDeadEnd(){
        return true;
    }
}
