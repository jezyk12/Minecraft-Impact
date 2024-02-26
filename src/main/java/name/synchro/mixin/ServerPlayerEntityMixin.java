package name.synchro.mixin;

import com.mojang.authlib.GameProfile;
import name.synchro.registrations.RegisterClientNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    @Unique private float syncedExhaustion = -114.514f;
    @Unique private int syncedFireTicks = -114514;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "playerTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getHealth()F",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private void sendExhaustionPacket(CallbackInfo ci){
        int fireTicks = this.getFireTicks();
        if (fireTicks != this.syncedFireTicks) {
            PacketByteBuf buf0 = PacketByteBufs.create();
            buf0.writeInt(fireTicks);
            ServerPlayNetworking.send((ServerPlayerEntity)(Object)this, RegisterClientNetworking.FIRE_TICKS_DATA_PACKET_ID, buf0);
            this.syncedFireTicks = fireTicks;
        }
        float saturation = this.getHungerManager().getSaturationLevel();
        float exhaustion = this.getHungerManager().getExhaustion();
        if (exhaustion != this.syncedExhaustion) {
            PacketByteBuf buf1 = PacketByteBufs.create();
            buf1.writeFloat(saturation);
            buf1.writeFloat(exhaustion);
            ServerPlayNetworking.send((ServerPlayerEntity)(Object)this, RegisterClientNetworking.HUNGER_DATA_PACKET_ID, buf1);
            this.syncedExhaustion = exhaustion;
        }
    }
}
