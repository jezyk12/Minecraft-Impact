package name.synchro.mixin.fluidConcerned;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import name.synchro.fluids.FluidUtil;
import name.synchro.playNetworking.ChunkFluidUpdateS2CPayload;
import name.synchro.playNetworking.SingleFluidUpdateS2CPayload;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {
    @Shadow @Final private ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;

    @Inject(method = "flushUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ChunkHolder;sendPacketToPlayers(Ljava/util/List;Lnet/minecraft/network/packet/Packet;)V",
            ordinal = 1, shift = At.Shift.AFTER))
    private void sendSingleFluidUpdatePacket(WorldChunk chunk, CallbackInfo ci, @Local BlockPos pos) {
        SingleFluidUpdateS2CPayload payload = new SingleFluidUpdateS2CPayload(pos, chunk.getWorld().getFluidState(pos));
        FluidUtil.sendToPlayersWatching(this.playersWatchingChunkProvider, chunk.getPos(), payload);
    }

    @Inject(method = "flushUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ChunkHolder;sendPacketToPlayers(Ljava/util/List;Lnet/minecraft/network/packet/Packet;)V",
            ordinal = 2, shift = At.Shift.AFTER))
    private void sendChunkFluidUpdatePacket(WorldChunk chunk, CallbackInfo ci, @Local ChunkSectionPos chunkSectionPos,
                                            @Local ShortSet shortSet, @Local ChunkSection chunkSection) {
        ChunkFluidUpdateS2CPayload payload = new ChunkFluidUpdateS2CPayload(chunkSectionPos, shortSet, chunkSection);
        FluidUtil.sendToPlayersWatching(this.playersWatchingChunkProvider, chunk.getPos(), payload);
    }

}
