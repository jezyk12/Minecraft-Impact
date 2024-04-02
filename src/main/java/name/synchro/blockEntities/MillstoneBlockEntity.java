package name.synchro.blockEntities;

import com.google.common.collect.ImmutableMap;
import name.synchro.registrations.RegisterBlockEntities;
import name.synchro.registrations.RegisterItems;
import name.synchro.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MillstoneBlockEntity extends BlockEntity implements SidedInventory, EntityEmployable, ProcessingTicker, RotationManager, BlockEntityExtraCollisionProvider {
    private static final String INVENTORY = "inventory";
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    private static final String PROCESSING_TICKS = "processing";
    private static final String ROTATION_NBT = "rotation";
    public static final String SHAPE_KEY_PREFIX = "millstone_";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final List<LivingEntity> employees = new ArrayList<>();
    private int processingTicks = 0;
    private final RotationManager.RotationProvider rotationProvider;
    private final Random random = new Random();
    private static final VoxelShape EXTRA_COLLISION = Block.createCuboidShape(-3, -3, -3, 3, 3, 3);
    public static final ImmutableMap<Item, ItemStack> MILLSTONE_RECIPES =
            ImmutableMap.of(
                    Blocks.OAK_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Blocks.SPRUCE_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Blocks.BIRCH_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Blocks.JUNGLE_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Blocks.ACACIA_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Blocks.DARK_OAK_PLANKS.asItem(), new ItemStack(RegisterItems.PLANT_FIBRE, 4),
                    Items.STICK, new ItemStack(RegisterItems.PLANT_FIBRE, 1)
            );

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.MILLSTONE_BLOCK_ENTITY, pos, state);
        this.rotationProvider = new RotationProvider(0L, 0);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtCompound inv = nbt.getCompound(INVENTORY);
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, ItemStack.fromNbt(inv.getCompound(String.valueOf(i))));
        }
        this.processingTicks = nbt.getInt(PROCESSING_TICKS);
        this.setupRotationFromNbt(nbt.getCompound(ROTATION_NBT));
    }

    @Override
    protected void writeNbt(NbtCompound toWriteNbt) {
        NbtCompound inv = new NbtCompound();
        for (int i = 0; i < this.inventory.size(); i++) {
            inv.put(String.valueOf(i), this.inventory.get(i).writeNbt(new NbtCompound()));
        }
        toWriteNbt.put(INVENTORY, inv);
        toWriteNbt.putInt(PROCESSING_TICKS, this.processingTicks);
        toWriteNbt.put(ROTATION_NBT, this.createRotationNbt());
        super.writeNbt(toWriteNbt);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case UP -> new int[]{SLOT_INPUT};
            case DOWN -> new int[]{SLOT_OUTPUT};
            default -> new int[0];
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.UP && slot == SLOT_INPUT) {
            return MILLSTONE_RECIPES.containsKey(stack.getItem());
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == SLOT_OUTPUT;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot).copy();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        Objects.requireNonNull(world).updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        Objects.requireNonNull(world).updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        return this.inventory.get(slot).split(this.inventory.get(slot).getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        Objects.requireNonNull(world).updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        Collections.fill(this.inventory, ItemStack.EMPTY);
        Objects.requireNonNull(world).updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
    }

    @Override
    public int getEmployeeMaxCount() {
        return 1;
    }

    @Override
    public List<LivingEntity> getEmployees() {
        return this.employees;
    }

    @Override
    public void setEmployees(List<LivingEntity> employees) {
        this.employees.clear();
        for (int i = 0; i < getEmployeeMaxCount(); i++) {
            this.employees.add(employees.get(i));
        }
    }

    @Override
    public void setEmployee(LivingEntity employee, int index) {
        this.employees.set(index, employee);
    }

    @Override
    public boolean hasEmployee() {
        return !this.employees.isEmpty();
    }

    @Override
    public boolean releaseEmployee(int index) {
        if (this.employees.get(index) != null) {
            this.employees.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public int getProcessingTicks() {
        return this.processingTicks;
    }

    @Override
    public void setProcessingTicks(int ticks) {
        this.processingTicks = ticks;
    }

    @Override
    public void tick() {
        boolean isWorking = this.rotationProvider.getSpeedMultiplier() > 0;
        if (MILLSTONE_RECIPES.get(this.getStack(SLOT_INPUT).getItem()) != null) {
            ItemStack processing = Objects.requireNonNull(MILLSTONE_RECIPES.get(this.getStack(SLOT_INPUT).getItem())).copy();
            ItemStack products = this.getStack(SLOT_OUTPUT);
            if (isWorking && (products.isEmpty() || (products.isOf(processing.getItem()) && products.getCount() + processing.getCount() <= products.getMaxCount()))) {
                this.processingTicks++;
                if (processingTicks >= 40) {
                    if (products.isEmpty()) {
                        this.setStack(SLOT_OUTPUT, processing);
                    } else {
                        products.increment(processing.getCount());
                        this.setStack(SLOT_OUTPUT, products);
                    }
                    this.removeStack(SLOT_INPUT,1);
                    this.processingTicks = 0;
                }
            }
        }
        else {
            if (this.processingTicks > 0)   this.processingTicks = 0;
        }
        if (world != null && world.isClient && isWorking && !this.getStack(SLOT_INPUT).isEmpty()){
            float y = pos.getY() + 12 / 16f;
            float r = 0.5f;
            float phi = random.nextFloat() * 360f;
            float dx = (float) (r * Math.cos(phi));
            float dz = (float) (r * Math.sin(phi));
            float x = pos.getX() + 0.5f + dx;
            float z = pos.getZ() + 0.5f + dz;
            world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack(SLOT_INPUT)), x, y ,z, dx / 10f, 0.05f, dz / 10f);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public RotationProvider getRotationProvider() {
        return this.rotationProvider;
    }

    @Override
    public Set<VoxelShape> getExtraCollisions() {
        HashSet<VoxelShape> extraCollisions = new HashSet<>();
        getExtraCollisionsOrigin(0f).forEach((voxelShape) ->
                extraCollisions.add(voxelShape.offset(this.pos.getX(), this.pos.getY(), this.pos.getZ())));
        return extraCollisions;
    }

    public Set<VoxelShape> getExtraCollisionsOrigin(float tickDelta){
        float rotation = this.getRecentRotation(Objects.requireNonNull(world).getTime(), tickDelta);
        double dx = 0.5 - Math.sin(Math.toRadians(rotation));
        double dy = 1.0625;
        double dz = 0.5 - Math.cos(Math.toRadians(rotation));
        String rotationKey = SHAPE_KEY_PREFIX + ((int)(720 - rotation + 0.5) % 180);
        return new HashSet<>(Collections.singletonList(IrregularVoxelShapes.getShape(rotationKey).offset(dx, dy, dz)));
    }

}
