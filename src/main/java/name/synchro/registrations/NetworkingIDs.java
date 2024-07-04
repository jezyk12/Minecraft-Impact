package name.synchro.registrations;

import name.synchro.Synchro;
import net.minecraft.util.Identifier;

public final class NetworkingIDs {
    public static final Identifier HUNGER_DATA_PACKET_ID = new Identifier(Synchro.MOD_ID, "hunger_data_packet");
    public static final Identifier FIRE_TICKS_DATA_PACKET_ID = new Identifier(Synchro.MOD_ID, "fire_ticks_data_packet");
    public static final Identifier MOD_DATA_PACK_ID = new Identifier(Synchro.MOD_ID, "mod_data_pack");
}
