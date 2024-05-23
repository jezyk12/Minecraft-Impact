package name.synchro;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

public class SynchroStandardStatic {
    public static final HashMap<Direction, BooleanProperty> DIRECTION_PROPERTY_MAP = new HashMap<>();
    public static void initialAll(){
        DIRECTION_PROPERTY_MAP.put(Direction.UP, Properties.UP);
        DIRECTION_PROPERTY_MAP.put(Direction.DOWN,Properties.DOWN);
        DIRECTION_PROPERTY_MAP.put(Direction.NORTH,Properties.NORTH);
        DIRECTION_PROPERTY_MAP.put(Direction.SOUTH,Properties.SOUTH);
        DIRECTION_PROPERTY_MAP.put(Direction.EAST,Properties.EAST);
        DIRECTION_PROPERTY_MAP.put(Direction.WEST,Properties.WEST);
        Synchro.LOGGER.debug("Initialized all static data for"+Synchro.MOD_ID);
    }
}
