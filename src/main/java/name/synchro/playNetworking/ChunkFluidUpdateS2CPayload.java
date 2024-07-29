package name.synchro.playNetworking;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import name.synchro.Synchro;
import name.synchro.fluids.FluidUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

import java.util.function.BiConsumer;

public record ChunkFluidUpdateS2CPayload(ChunkSectionPos sectionPos, short[] positions, FluidState[] fluidStates) implements ModPayload {
    public static final Identifier CHUNK_FLUID_UPDATE_ID = Identifier.of(Synchro.MOD_ID, "chunk_fluid_update");
    public static final Id<ChunkFluidUpdateS2CPayload> ID = new Id<>(CHUNK_FLUID_UPDATE_ID);
    public static final PacketCodec<PacketByteBuf, ChunkFluidUpdateS2CPayload> CODEC = PacketCodec.of(ChunkFluidUpdateS2CPayload::encode, ChunkFluidUpdateS2CPayload::decode);
    public ChunkFluidUpdateS2CPayload(ChunkSectionPos sectionPos, ShortSet positionsSet, ChunkSection section) {
        this(sectionPos, new short[positionsSet.size()], new FluidState[positionsSet.size()]);
        int index = 0;
        for(ShortIterator iterator = positionsSet.iterator(); iterator.hasNext(); ++index) {
            short packedLocalPos = iterator.nextShort();
            positions[index] = packedLocalPos;
            int localX = ChunkSectionPos.unpackLocalX(packedLocalPos);
            int localY = ChunkSectionPos.unpackLocalY(packedLocalPos);
            int localZ = ChunkSectionPos.unpackLocalZ(packedLocalPos);
            fluidStates[index] = section.getFluidState(localX, localY, localZ);
        }
    }

    private static ChunkFluidUpdateS2CPayload decode(PacketByteBuf buf) {
        ChunkSectionPos sectionPos = ChunkSectionPos.from(buf.readLong());
        int i = buf.readVarInt();
        short[] positions = new short[i];
        FluidState[] fluidStates = new FluidState[i];
        for(int j = 0; j < i; ++j) {
            long l = buf.readVarLong();
            positions[j] = (short)((int)(l & 4095L));
            fluidStates[j] = Fluid.STATE_IDS.get((int)(l >>> 12));
        }
        return new ChunkFluidUpdateS2CPayload(sectionPos, positions, fluidStates);
    }

    private static void encode(ChunkFluidUpdateS2CPayload payload, PacketByteBuf buf) {
        buf.writeLong(payload.sectionPos().asLong());
        buf.writeVarInt(payload.positions().length);
        for(int i = 0; i < payload.positions().length; ++i) {
            buf.writeVarLong((long) FluidUtil.getRawIdFromState(payload.fluidStates()[i]) << 12 | (long)payload.positions()[i]);
        }
    }

    public void forEach(BiConsumer<BlockPos, FluidState> consumer) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int i = 0; i < this.positions.length; ++i) {
            short packedPos = this.positions[i];
            int x = this.sectionPos.unpackBlockX(packedPos);
            int y = this.sectionPos.unpackBlockY(packedPos);
            int z = this.sectionPos.unpackBlockZ(packedPos);
            mutable.set(x, y, z);
            consumer.accept(mutable, this.fluidStates[i]);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends ModPayload> getCodec() {
        return CODEC;
    }
}
