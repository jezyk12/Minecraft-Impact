package name.synchro.fluids;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

@Deprecated
public class ChunkDeltaUpdateWithFluidS2CPacket implements Packet<ClientPlayPacketListener> {
    final ChunkSectionPos sectionPos;
    final short[] positions;
    final BlockState[] blockStates;
    final boolean noLightingUpdates;
    final FluidState[] fluidStates;

    public ChunkDeltaUpdateWithFluidS2CPacket(PacketByteBuf buf) {
        this.sectionPos = ChunkSectionPos.from(buf.readLong());
        this.noLightingUpdates = buf.readBoolean();
        int size = buf.readVarInt();
        this.positions = new short[size];
        this.blockStates = new BlockState[size];
        this.fluidStates = new FluidState[size];

        for(int j = 0; j < size; ++j) {
            long l = buf.readVarLong();
            this.positions[j] = (short)((int)(l & 4095L));
            this.blockStates[j] = Block.STATE_IDS.get((int)((l >>> 12) & 0xfffffL));
            this.fluidStates[j] = Fluid.STATE_IDS.get((int)(l >>> 32));
        }
    }

    public ChunkDeltaUpdateWithFluidS2CPacket(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section, boolean noLightingUpdates) {
        this.sectionPos = sectionPos;
        this.noLightingUpdates = noLightingUpdates;
        int size = positions.size();
        this.positions = new short[size];
        this.blockStates = new BlockState[size];
        this.fluidStates = new FluidState[size];
        int j = 0;
        for(ShortIterator posIterator = positions.iterator(); posIterator.hasNext(); ++j) {
            short packedLocalPos = posIterator.next();
            this.positions[j] = packedLocalPos;
            int localX = ChunkSectionPos.unpackLocalX(packedLocalPos);
            int localY = ChunkSectionPos.unpackLocalY(packedLocalPos);
            int localZ = ChunkSectionPos.unpackLocalZ(packedLocalPos);
            this.blockStates[j] = section.getBlockState(localX, localY, localZ);
            this.fluidStates[j] = section.getFluidState(localX, localY, localZ);
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeBoolean(this.noLightingUpdates);
        buf.writeVarInt(this.positions.length);

        for(int i = 0; i < this.positions.length; ++i) {
            buf.writeVarLong((long) FluidUtil.getRawIdFromState(this.fluidStates[i]) << 32
                            | (long) Block.getRawIdFromState(this.blockStates[i]) << 12
                            | (long) this.positions[i]);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ((FluidHelper.ForClientPacketListener) listener).onChunkUpdateWithFluid(this);
    }

    public void visitUpdates(Visitor visitor) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int i = 0; i < this.positions.length; ++i) {
            short packedPos = this.positions[i];
            int x = this.sectionPos.unpackBlockX(packedPos);
            int y = this.sectionPos.unpackBlockY(packedPos);
            int z = this.sectionPos.unpackBlockZ(packedPos);
            mutable.set(x, y, z);
            visitor.accept(mutable, this.blockStates[i], this.fluidStates[i]);
        }
    }

    public boolean shouldSkipLightingUpdates() {
        return this.noLightingUpdates;
    }

    @FunctionalInterface
    public interface Visitor{
        void accept(BlockPos pos, BlockState blockState, FluidState fluidState);
    }
}
