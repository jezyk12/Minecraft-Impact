package name.synchro.blockEntities;

import com.google.common.collect.ImmutableMap;
import name.synchro.employment.BlockEntityWorkerManager;
import name.synchro.employment.Employer;
import name.synchro.employment.Job;
import name.synchro.registrations.RegisterBlockEntities;
import name.synchro.registrations.RegisterItems;
import name.synchro.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MillstoneBlockEntity extends BlockEntity implements SidedInventory, ProcessingHandler, Rotatable, BlockEntityExtraCollisionProvider, Employer {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final String WOOD_PREFIX = "millstone_wood:";
    public static final String TOP_PREFIX = "millstone_top:";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private int processingTime = 0;
    private final RotationProvider rotationProvider;
    private final Random random = new Random();
    private final MillstoneWorkerManager workerManager;
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
        this.workerManager = new MillstoneWorkerManager();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtCompound inv = nbt.getCompound(NbtTags.INVENTORY);
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, ItemStack.fromNbt(inv.getCompound(String.valueOf(i))));
        }
        this.processingTime = nbt.getInt(NbtTags.PROCESSING_TICKS);
        this.setupRotationFromNbt(nbt.getCompound(NbtTags.ROTATION), world);
        this.workerManager.setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYEES));
    }

    @Override
    protected void writeNbt(NbtCompound toWriteNbt) {
        NbtCompound inv = new NbtCompound();
        for (int i = 0; i < this.inventory.size(); i++) {
            inv.put(String.valueOf(i), this.inventory.get(i).writeNbt(new NbtCompound()));
        }
        toWriteNbt.put(NbtTags.INVENTORY, inv);
        toWriteNbt.putInt(NbtTags.PROCESSING_TICKS, this.processingTime);
        toWriteNbt.put(NbtTags.ROTATION, this.createRotationNbt(Objects.requireNonNull(world).getTime()));
        toWriteNbt.put(NbtTags.EMPLOYEES, this.workerManager.getEmploymentNbt());
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
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.inventory.get(slot).split(this.inventory.get(slot).getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
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
    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public void setProcessingTime(int ticks) {
        this.processingTime = ticks;
    }

    @Override
    public void tick() {
        int processMultiplier = Math.abs(this.rotationProvider.getSpeedMultiplier());
        boolean isWorking = processMultiplier > 0;
        if (MILLSTONE_RECIPES.get(this.getStack(SLOT_INPUT).getItem()) != null) {
            ItemStack processing = Objects.requireNonNull(MILLSTONE_RECIPES.get(this.getStack(SLOT_INPUT).getItem())).copy();
            ItemStack products = this.getStack(SLOT_OUTPUT).copy();
            if (isWorking && canProcess(products, processing)) {
                this.processingTime += processMultiplier;
                if (world instanceof ServerWorld serverWorld){
                    float y = pos.getY() + 12 / 16f;
                    float r = 0.5f;
                    float phi = random.nextFloat() * 360f;
                    float dx = (float) (r * Math.cos(phi));
                    float dz = (float) (r * Math.sin(phi));
                    float x = pos.getX() + 0.5f + dx;
                    float z = pos.getZ() + 0.5f + dz;
                    serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack(SLOT_INPUT).copy()), x, y ,z, 1, dx / 10f, 0.05f, dz / 10f, 0.1f);
                }
                if (processingTime >= 200) {
                    if (products.isEmpty()) {
                        this.setStack(SLOT_OUTPUT, processing);
                    } else {
                        products.increment(processing.getCount());
                        this.setStack(SLOT_OUTPUT, products);
                    }
                    this.removeStack(SLOT_INPUT,1);
                    this.processingTime = 0;
                }
            }
        }
        else {
            if (this.processingTime > 0)   this.processingTime = 0;
        }
        if (isWorking) {
            tryPushingEntity();
        }
     }

    public static boolean canProcess(ItemStack products, ItemStack processing) {
        return (products.isEmpty() || (products.isOf(processing.getItem()) && products.getCount() + processing.getCount() <= products.getMaxCount()));
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
    public void updateSpeedMultiplier(long timeNow, int newSpeedMultiplier) {
        Rotatable.super.updateSpeedMultiplier(timeNow, newSpeedMultiplier);
        markDirty();
        if (world != null) {
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        }
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
        double dx = 0.5 - Math.sin(Math.toRadians(rotation)) * 1.5;
        double dy = 1.0625;
        double dz = 0.5 - Math.cos(Math.toRadians(rotation)) * 1.5;
        String rotationValue = String.valueOf(((int)(720 - rotation + 0.5) % 180));
        VoxelShape top = IrregularVoxelShapes.getShape(TOP_PREFIX + rotationValue).offset(0.5, 1.0625, 0.5);
        VoxelShape wood = IrregularVoxelShapes.getShape(WOOD_PREFIX + rotationValue).offset(dx, dy, dz);
        return new HashSet<>(Arrays.asList(top, wood));
    }

    private void tryPushingEntity(){
        if (world != null) {
            HashSet<Box> boxes = new HashSet<>();
            getExtraCollisions().forEach((voxelShape) -> boxes.addAll(voxelShape.getBoundingBoxes()));
            double rotation = Math.toRadians(getRecentRotation(world.getTime()));
            double vx = - Math.cos(rotation) * Math.PI / 180;
            double vz = Math.sin(rotation) * Math.PI / 180;
            int speed = getRotationProvider().getSpeedMultiplier();
            Set<Entity> entities = new HashSet<>();
            for (Box box : boxes){
                entities.addAll(world.getOtherEntities(null, box.stretch(vx, 0.0625, vz)));
            }
            for (Entity entity : entities){
                if (entity.getPistonBehavior() != PistonBehavior.IGNORE){
                    double dx = entity.getX() - pos.getX() - 0.5;
                    double dz = entity.getZ() - pos.getZ() - 0.5;
                    double r = Math.sqrt(dx * dx + dz * dz);
                    Vec3d movement = new Vec3d(vx * r * speed, 0, vz * r * speed);
                    entity.move(MovementType.SELF, movement);
                }
            }
        }
    }

    @Override
    public MillstoneWorkerManager getWorkerManager() {
        return this.workerManager;
    }

    public class MillstoneWorkerManager extends BlockEntityWorkerManager {
        public MillstoneWorkerManager() {
            super(2, pos.toCenterPos());
        }

        @Override
        public Job providingJob() {
            return Job.PUSH_MILLSTONE;
        }

        public Vec3d polePos(double r){
            assert world != null;
            double rotation = Math.toRadians(getRecentRotation(world.getTime()));
            double dx = - Math.sin(rotation) * r;
            double dy = 0.5625;
            double dz = - Math.cos(rotation) * r;
            return pos.toCenterPos().add(dx, dy, dz);
        }

        public Vec3d getNextStepPos(Vec3d mobPos){
            if (world != null) {
                int rotBlock = getRecentRotation(world.getTime());
                double vx = -Math.cos(Math.toRadians(rotBlock));
                double vz = Math.sin(Math.toRadians(rotBlock));
                double rotDiff = getDegreeDiff(rotBlock, mobPos);
                if (rotDiff >= 0) {
                    if (rotDiff >= 90) {
                        return polePos(2).subtract(vx, 0, vz);
                    } else return polePos(2).add(vx, 0, vz);
                } else {
                    if (rotDiff <= -90) {
                        return polePos(2).add(vx, 0, vz);
                    } else return polePos(2).subtract(vx, 0, vz);
                }
            }
            return pos.toCenterPos();
        }

        public int getDegreeDiff(int rotBlock, Vec3d mobPos){
            Vec3d vecMob = mobPos.subtract(pos.toCenterPos());
            int rotMob = (int) Math.toDegrees(Math.atan2(vecMob.x, vecMob.z));
            int degDiff = Math.floorMod(rotBlock + 180 - rotMob, 360);
            return degDiff >= 180 ? degDiff - 360 : degDiff;
        }
    }
}
