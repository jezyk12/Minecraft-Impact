package name.synchro.blockEntities;

import name.synchro.Synchro;
import name.synchro.employment.BlockEntityWorkerManager;
import name.synchro.employment.Employer;
import name.synchro.employment.Job;
import name.synchro.registrations.ModBlockEntities;
import name.synchro.registrations.ModItems;
import name.synchro.screenHandlers.MillstoneScreenHandler;
import name.synchro.specialRecipes.MillstoneRecipe;
import name.synchro.util.*;
import name.synchro.modUtilData.dataEntries.CowWorkingFeedsData;
import name.synchro.modUtilData.ModDataContainer;
import name.synchro.modUtilData.ModDataManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MillstoneBlockEntity extends BlockEntity implements SidedInventory, Rotatable, BlockEntityExtraCollisionProvider, Employer, NamedScreenHandlerFactory {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_FEED = 2;
    public static final int INV_SIZE = 3;
    public static final String WOOD_PREFIX = "millstone_wood:";
    public static final String TOP_PREFIX = "millstone_top:";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
    private int processingDegrees = 0;
    private int progressOf24 = 0;
    private final RotationProvider rotationProvider;
    private final Random random = new Random();
    private final MillstoneWorkerManager workerManager;
    private boolean locked = false;
    @Nullable private MillstoneRecipe processingRecipe;
    private ItemStack cacheInputStack = ItemStack.EMPTY;
    public static final int INT_PROGRESS = 0;
    public static final int INT_SPEED = 1;
    public static final int INTEGERS_SIZE = 2;
    private final PropertyDelegate propertyDelegate;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MILLSTONE_BLOCK_ENTITY, pos, state);
        this.rotationProvider = new RotationProvider(0L, 0);
        this.propertyDelegate = new MillstonePropertyDelegate();
        this.workerManager = new MillstoneWorkerManager();
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);
        NbtCompound inv = nbt.getCompound(NbtTags.INVENTORY);
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, ModItems.decode(inv.getCompound(String.valueOf(i)), wrapperLookup));
        }
        this.processingDegrees = nbt.getInt(NbtTags.PROCESSING_TICKS);
        this.setupRotationFromNbt(nbt.getCompound(NbtTags.ROTATION), world);
        this.workerManager.setEmploymentFromNbt(nbt.getCompound(NbtTags.EMPLOYEES));
    }

    @Override
    protected void writeNbt(NbtCompound toWriteNbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound inv = new NbtCompound();
        for (int i = 0; i < this.inventory.size(); i++) {
            inv.put(String.valueOf(i), ModItems.encode(this.inventory.get(i), wrapperLookup));
        }
        toWriteNbt.put(NbtTags.INVENTORY, inv);
        toWriteNbt.putInt(NbtTags.PROCESSING_TICKS, this.processingDegrees);
        toWriteNbt.put(NbtTags.ROTATION, this.createRotationNbt(Objects.requireNonNull(world).getTime()));
        toWriteNbt.put(NbtTags.EMPLOYEES, this.workerManager.getEmploymentNbt());
        super.writeNbt(toWriteNbt, wrapperLookup);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world instanceof ImportantPoints.Provider pointsView){
            pointsView.getImportantPoints().get(ImportantPoints.Type.EXTRA_COLLISION).add(pos);
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (world instanceof ImportantPoints.Provider pointsView){
            pointsView.getImportantPoints().get(ImportantPoints.Type.EXTRA_COLLISION).remove(pos);
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{SLOT_INPUT, SLOT_FEED};
        }
        else return new int[]{SLOT_OUTPUT};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.UP ){
            if (slot == SLOT_INPUT) {
                return canInsertForProcessing(world, stack);
            }
            else if (slot == SLOT_FEED && world != null) {
                return isFeed(world, stack);
            }
        }
        return false;
    }

    public static boolean canInsertForProcessing(World world, ItemStack stack){
        if (world != null){
            return world.getRecipeManager().listAllOfType(MillstoneRecipe.TYPE)
                    .stream().anyMatch(recipe -> recipe.value().input().test(stack));
        }
        return false;
    }

    public static boolean isFeed(World world, ItemStack stack){
        if (world != null){
            ModDataContainer<?> container = ((ModDataManager.Provider)(world)).synchro$getModDataManager().getContents().get(CowWorkingFeedsData.ID);
            if (container instanceof CowWorkingFeedsData cowData){
                return cowData.data().stream().anyMatch(entry -> entry.items().test(stack));
            }
        }
        return false;
    }

    public static int getFeedTime(World world, ItemStack stack){
        if (world != null){
            ModDataContainer<?> container = ((ModDataManager.Provider)(world)).synchro$getModDataManager().getContents().get(CowWorkingFeedsData.ID);
            if (container instanceof CowWorkingFeedsData cowData){
                return Math.max(cowData.data().stream().filter(entry -> entry.items().test(stack)).map(CowWorkingFeedsData.Entry::time)
                        .max(Integer::compareTo).orElse(100), 100);
            }
        }
        return 100;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == SLOT_OUTPUT;
    }

    @Override
    public int size() {
        return INV_SIZE;
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

    public void tick() {
        if (world != null){
            int processMultiplier = Math.abs(this.rotationProvider.getSpeedMultiplier());
            boolean isWorking = processMultiplier > 0;
            ItemStack inputStack = this.getStack(SLOT_INPUT);
            if (!inputStack.isOf(cacheInputStack.getItem())) {
                if (!inputStack.isEmpty()) {
                    Optional<RecipeEntry<MillstoneRecipe>> entryOptional = world.getRecipeManager().getFirstMatch(MillstoneRecipe.TYPE, MillstoneRecipe.Input.of(this.getStack(SLOT_INPUT)), world);
                    this.processingRecipe = entryOptional.map(RecipeEntry::value).orElse(null);
                }
                else this.processingRecipe = null;
                this.cacheInputStack = inputStack.copy();
            }
            if (isWorking) {
                if (this.processingRecipe != null) {
                    ItemStack processing = this.processingRecipe.getActualOutput(MillstoneRecipe.Input.of(this.getStack(SLOT_INPUT)));
                    int degrees = this.processingRecipe.getDegrees();
                    ItemStack products = this.getStack(SLOT_OUTPUT).copy();
                    tryProcess(products, processing, processMultiplier, degrees);
                } else {
                    if (this.processingDegrees > 0) {
                        this.processingDegrees = 0;
                        this.progressOf24 = 0;
                    }
                }
                tryPushingEntity();
                markDirty();
            }
        }
     }

    private void tryProcess(ItemStack products, ItemStack processing, int processMultiplier, int endDegrees) {
        if (notJammed(products, processing)) {
            this.processingDegrees += processMultiplier;
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
            if (processingDegrees >= endDegrees) {
                ItemStackUtil.Result result = ItemStackUtil.transfer(processing, products);
                this.setStack(SLOT_OUTPUT, result.existingStackAfter());
                this.removeStack(SLOT_INPUT,1);
                this.processingDegrees = 0;
            }
            this.progressOf24 = this.processingDegrees * 24 / endDegrees;
        }
    }

    public boolean isFollowingRecipe(){
        return this.processingRecipe != null;
    }

    public static boolean notJammed(ItemStack products, ItemStack processing) {
        return ItemStackUtil.canCompletelyMerge(processing, products);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
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

    @Override
    public Text getDisplayName() {
        return Text.translatable(this.getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MillstoneScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    public void lock(){
        this.locked = true;
    }

    public void unlock(){
        this.locked = false;
    }

    public boolean isLocked(){
        return this.locked;
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

    private class MillstonePropertyDelegate implements PropertyDelegate {
        @Override
        public int get(int index) {
            if (index == INT_PROGRESS) {
                return progressOf24;
            }
            else if (index == INT_SPEED) {
                return rotationProvider.getSpeedMultiplier();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            Synchro.LOGGER.warn("MillstonePropertyDelegate does not support setting values.");
        }

        @Override
        public int size() {
            return INTEGERS_SIZE;
        }
    }
}
