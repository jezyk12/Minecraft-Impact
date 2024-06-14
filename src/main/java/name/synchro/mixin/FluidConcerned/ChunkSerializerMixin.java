package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @WrapOperation(method = "deserialize", at = @At(value = "NEW",
            target = "net/minecraft/world/chunk/ChunkSection"))
    private static ChunkSection deserializeFluidStatePaletteContainer(
            int y, PalettedContainer<BlockState> blockStateContainer, ReadableContainer<RegistryEntry<Biome>> biomeContainer, Operation<ChunkSection> original,
            @Local(ordinal = 1) NbtCompound nbtCompound, @Local(ordinal = 1) ChunkPos chunkPos){
        ChunkSection instance = new ChunkSection(y, blockStateContainer, biomeContainer);
        modifyNewChunkSection(instance, nbtCompound, chunkPos);
        return instance;
    }

    @Unique
    private static @NotNull ChunkSection modifyNewChunkSection(ChunkSection chunkSection, NbtCompound nbtCompound, ChunkPos chunkPos) {
        int yPos = chunkSection.getYOffset() >> 4;
        PalettedContainer<FluidState> fluidStateContainer = FluidUtil.deserialize(nbtCompound, chunkPos, yPos);
        ((FluidHelper.ForChunkSection) chunkSection).setFluidStateContainer(fluidStateContainer);
        ((FluidHelper.ForChunkSection) chunkSection).countFluidStates();
        return chunkSection;
    }

    @Inject(method = "serialize", at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
            ordinal = 3, shift = At.Shift.BEFORE))
    private static void serializeFluidStatePaletteContainer(
            ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir,
            @Local ChunkSection chunkSection, @Local(ordinal = 1) NbtCompound nbtCompound){
        FluidUtil.serialize(nbtCompound, chunkSection);
    }
}
