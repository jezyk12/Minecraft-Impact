package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.BlockUpdateWithFluidS2CPacket;
import name.synchro.fluids.ChunkDeltaUpdateWithFluidS2CPacket;
import name.synchro.fluids.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Deprecated
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayPacketListenerHandlerMixin implements FluidHelper.ForClientPacketListener {
    @Shadow @Final private MinecraftClient client;

    @Shadow private ClientWorld world;

    @Override
    public void onChunkUpdateWithFluid(ChunkDeltaUpdateWithFluidS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ClientPlayPacketListener)this, this.client);
        int flags = Block.NOTIFY_ALL | Block.FORCE_STATE | (packet.shouldSkipLightingUpdates() ? Block.SKIP_LIGHTING_UPDATES : 0);
        packet.visitUpdates((pos, blockState, fluidState) ->
                ((FluidHelper.ForClientWorld) this.world).handleBlockUpdateWithFluid(pos, blockState, fluidState, flags));
    }

    @Override
    public void onBlockUpdateWithFluid(BlockUpdateWithFluidS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ClientPlayPacketListener) this, this.client);
        ((FluidHelper.ForClientWorld) this.world).handleBlockUpdateWithFluid(packet.getPos(), packet.getState(), packet.getFluidState(), 19);
    }
}
