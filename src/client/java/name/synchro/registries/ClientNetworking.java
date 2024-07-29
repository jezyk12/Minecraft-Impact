package name.synchro.registries;

import name.synchro.Synchro;
import name.synchro.playNetworking.*;
import name.synchro.util.ClientFluidUtil;
import name.synchro.util.ClientModDataManager;
import name.synchro.util.NewHudUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {
    public static void registerReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(HungerDataPayload.ID, NewHudUtil::receiveHungerDataPacket);
        ClientPlayNetworking.registerGlobalReceiver(FireTicksDataPayload.ID, NewHudUtil::receiveFireTicksDataPacket);
        ClientPlayNetworking.registerGlobalReceiver(ModDataPayload.ID, ClientModDataManager::receiveData);
        ClientPlayNetworking.registerGlobalReceiver(SingleFluidUpdateS2CPayload.ID, ClientFluidUtil::onSingleFluidUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ChunkFluidUpdateS2CPayload.ID, ClientFluidUtil::onChunkFluidUpdate);    }

    public static void registerAll(){
        registerReceivers();
        Synchro.LOGGER.debug("Registered client networking for "+ Synchro.MOD_ID);
    }
}
