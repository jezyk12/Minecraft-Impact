package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.playNetworking.ChunkFluidUpdateS2CPayload;
import name.synchro.playNetworking.FireTicksDataPayload;
import name.synchro.playNetworking.HungerDataPayload;
import name.synchro.playNetworking.SingleFluidUpdateS2CPayload;
import name.synchro.util.ClientFluidUtil;
import name.synchro.util.NewHudUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {
    public static void registerReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(HungerDataPayload.ID, NewHudUtil::receiveHungerDataPacket);
        ClientPlayNetworking.registerGlobalReceiver(FireTicksDataPayload.ID, NewHudUtil::receiveFireTicksDataPacket);
        ClientPlayNetworking.registerGlobalReceiver(SingleFluidUpdateS2CPayload.ID, ClientFluidUtil::onSingleFluidUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ChunkFluidUpdateS2CPayload.ID, ClientFluidUtil::onChunkFluidUpdate);    }

    public static void registerAll(){
        registerReceivers();
        Synchro.LOGGER.debug("Registered client networking for "+ Synchro.MOD_ID);
    }
}
