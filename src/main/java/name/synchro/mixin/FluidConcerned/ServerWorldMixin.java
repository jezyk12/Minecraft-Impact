package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.modUtilData.ModDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ModDataManager.Provider {
    @Shadow @NotNull public abstract MinecraftServer getServer();

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target= "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;"))
    private FluidState fixGetFluidState(BlockState instance, Operation<FluidState> original,
                                        @Local(ordinal = 0) BlockPos blockPos, @Local ChunkSection chunkSection) {
        return chunkSection.getFluidState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
    }

    @Override
    public ModDataManager synchro$getModDataManager() {
        return ((ModDataManager.Provider) this.getServer()).synchro$getModDataManager();
    }
}
