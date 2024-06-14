package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.fluids.ChunkFluidUpdateS2CPacket;
import name.synchro.fluids.FluidUtil;
import name.synchro.fluids.SingleFluidUpdateS2CPacket;
import name.synchro.util.NewHudUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public final class RegisterClientNetworking {
    public static final Identifier HUNGER_DATA_PACKET_ID = register("hunger_data_packet", NewHudUtil::receiveHungerDataPacket);
    public static final Identifier FIRE_TICKS_DATA_PACKET_ID = register("fire_ticks_data_packet", NewHudUtil::receiveFireTicksDataPacket);

    private static Identifier register(String path, ClientPlayNetworking.PlayChannelHandler handler){
        Identifier id = new Identifier(Synchro.MOD_ID, path);
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
        return id;
    }

    public static void registerAll(){
        ClientPlayNetworking.registerGlobalReceiver(SingleFluidUpdateS2CPacket.TYPE, FluidUtil::onSingleFluidUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ChunkFluidUpdateS2CPacket.TYPE, FluidUtil::onChunkFluidUpdate);
        Synchro.LOGGER.debug("Registered client networking for "+ Synchro.MOD_ID);
    }
}
