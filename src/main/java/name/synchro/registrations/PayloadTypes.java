package name.synchro.registrations;

import name.synchro.playNetworking.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class PayloadTypes {
    public static void registerAll() {
        PayloadTypeRegistry.playS2C().register(HungerDataPayload.ID, HungerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FireTicksDataPayload.ID, FireTicksDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ModDataPayload.ID, ModDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SingleFluidUpdateS2CPayload.ID, SingleFluidUpdateS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ChunkFluidUpdateS2CPayload.ID, ChunkFluidUpdateS2CPayload.CODEC);
    }
}
