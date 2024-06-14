package name.synchro.fluids;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

import java.util.function.BiConsumer;

public class ChunkFluidUpdateS2CPacket implements FabricPacket {
    final ChunkSectionPos sectionPos;
    final short[] positions;
    final FluidState[] fluidStates;

    public static final PacketType<ChunkFluidUpdateS2CPacket> TYPE =
            PacketType.create(new Identifier("chunk_fluid_update"), ChunkFluidUpdateS2CPacket::new);

    ChunkFluidUpdateS2CPacket(PacketByteBuf buf) {
        this.sectionPos = ChunkSectionPos.from(buf.readLong());
        int i = buf.readVarInt();
        this.positions = new short[i];
        this.fluidStates = new FluidState[i];
        for(int j = 0; j < i; ++j) {
            long l = buf.readVarLong();
            this.positions[j] = (short)((int)(l & 4095L));
            this.fluidStates[j] = Fluid.STATE_IDS.get((int)(l >>> 12));
        }
    }

    public ChunkFluidUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section) {
        this.sectionPos = sectionPos;
        int size = positions.size();
        this.positions = new short[size];
        this.fluidStates = new FluidState[size];
        int index = 0;
        for(ShortIterator iterator = positions.iterator(); iterator.hasNext(); ++index) {
            short packedLocalPos = iterator.next();
            this.positions[index] = packedLocalPos;
            int localX = ChunkSectionPos.unpackLocalX(packedLocalPos);
            int localY = ChunkSectionPos.unpackLocalY(packedLocalPos);
            int localZ = ChunkSectionPos.unpackLocalZ(packedLocalPos);
            this.fluidStates[index] = section.getFluidState(localX, localY, localZ);
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeVarInt(this.positions.length);
        for(int i = 0; i < this.positions.length; ++i) {
            buf.writeVarLong((long) FluidUtil.getRawIdFromState(this.fluidStates[i]) << 12 | (long)this.positions[i]);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
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
}
