package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.fluids.ChunkFluidUpdateS2CPacket;
import name.synchro.fluids.FluidUtil;
import name.synchro.fluids.SingleFluidUpdateS2CPacket;
import name.synchro.util.NewHudUtil;
import name.synchro.util.dataDriven.ModDataManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {
    public static Identifier register(Identifier id, ClientPlayNetworking.PlayChannelHandler handler){
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
        return id;
    }

    public static void registerAll(){
        register(NetworkingIDs.HUNGER_DATA_PACKET_ID, NewHudUtil::receiveHungerDataPacket);
        register(NetworkingIDs.FIRE_TICKS_DATA_PACKET_ID, NewHudUtil::receiveFireTicksDataPacket);
        register(NetworkingIDs.MOD_DATA_PACK_ID, ModDataManager.ForClient::receiveData);
        ClientPlayNetworking.registerGlobalReceiver(SingleFluidUpdateS2CPacket.TYPE, FluidUtil::onSingleFluidUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ChunkFluidUpdateS2CPacket.TYPE, FluidUtil::onChunkFluidUpdate);
        Synchro.LOGGER.debug("Registered client networking for "+ Synchro.MOD_ID);
    }
}
