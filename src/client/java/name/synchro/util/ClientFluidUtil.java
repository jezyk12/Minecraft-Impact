package name.synchro.util;

import name.synchro.fluids.FluidHelper;
import name.synchro.playNetworking.ChunkFluidUpdateS2CPayload;
import name.synchro.playNetworking.SingleFluidUpdateS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientFluidUtil {
    public static void onSingleFluidUpdate(SingleFluidUpdateS2CPayload payload, ClientPlayNetworking.Context context){
        ((FluidHelper.ForWorld) context.player().clientWorld).synchro$setFluidState(payload.pos(), payload.fluidState(), 0b10011, 512);
    }

    public static void onChunkFluidUpdate(ChunkFluidUpdateS2CPayload packet, ClientPlayNetworking.Context context){
        packet.forEach((pos, state) -> ((FluidHelper.ForWorld) context.player().clientWorld).synchro$setFluidState(pos, state, 0b10011, 512));
    }
}
