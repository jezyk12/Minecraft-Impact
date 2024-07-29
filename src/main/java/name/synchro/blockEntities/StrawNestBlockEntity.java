package name.synchro.blockEntities;

import name.synchro.employment.BlockEntityWorkerManager;
import name.synchro.employment.Employer;
import name.synchro.employment.StrawNestWorkerManager;
import name.synchro.registrations.ModBlockEntities;
import name.synchro.registrations.ModItems;
import name.synchro.util.NbtTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class StrawNestBlockEntity extends BlockEntity implements SidedInventory, Employer {
    public static final int[] SLOTS = {0};
    private final BlockEntityWorkerManager workerManager;
    ItemStack eggSlot = ItemStack.EMPTY;
    public StrawNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STRAW_NEST_BLOCK_ENTITY, pos, state);
        this.workerManager = new StrawNestWorkerManager(1, pos);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return eggSlot.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (world != null) {
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        }
        return eggSlot;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = eggSlot.split(amount);
        if (world != null) {
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        }
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, getStack(slot).getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        eggSlot = stack;
        if (world != null) {
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        eggSlot = ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);
        NbtElement eggSlotNbt = nbt.getCompound(NbtTags.EGG_SLOT);
        this.eggSlot = ModItems.decode(eggSlotNbt, wrapperLookup);
        this.workerManager.setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYEES));
    }

    @Override
    protected void writeNbt(NbtCompound toWriteNbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtElement eggSlotNbt = ModItems.encode(this.eggSlot, wrapperLookup);
        toWriteNbt.put(NbtTags.EGG_SLOT, eggSlotNbt);
        toWriteNbt.put(NbtTags.EMPLOYEES, this.workerManager.getEmploymentNbt());
        super.writeNbt(toWriteNbt, wrapperLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public BlockEntityWorkerManager getWorkerManager() {
        return this.workerManager;
    }

}
