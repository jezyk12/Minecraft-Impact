package name.synchro.mixin.FluidConcerned;

import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSection.class)
public abstract class ChunkSectionMixin implements FluidHelper.ForChunkSection {
    @Shadow private short nonEmptyFluidCount;

    @Shadow public abstract PalettedContainer<BlockState> getBlockStateContainer();

    @Unique
    private PalettedContainer<FluidState> fluidStateContainer;

    @Inject(method = "<init>(ILnet/minecraft/registry/Registry;)V", at = @At("RETURN"))
    private void initEmptyFluidStorage(int chunkPos, Registry<Biome> biomeRegistry, CallbackInfo ci) {
        this.fluidStateContainer = FluidUtil.createFluidStatePaletteContainer();
    }

    @Override
    public PalettedContainer<FluidState> getFluidStateContainer() {
        return this.fluidStateContainer;
    }

    @Override
    public void setFluidStateContainer(PalettedContainer<FluidState> container) {
        this.fluidStateContainer = container;
    }

    @Override
    public void countFluidStates() {
        this.nonEmptyFluidCount = (short) FluidUtil.countFluidStates(this.getFluidStateContainer());
    }

    @Inject(method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;",
            at = @At(value = "HEAD"), cancellable = true)
    private void setBlockStateExtra(int x, int y, int z, BlockState blockState, boolean lock, CallbackInfoReturnable<BlockState> cir){
        if (blockState.getBlock() instanceof FluidBlock){
            setFluidState(x, y, z, blockState.getFluidState(), lock, true);
            cir.setReturnValue(Blocks.AIR.getDefaultState());
            cir.cancel();
        }
        else if (blockState.getBlock() instanceof Waterloggable){
            if (blockState.get(Properties.WATERLOGGED)){
                setFluidState(x, y, z, Fluids.WATER.getDefaultState(), lock, false);
            }
        }
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void getFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir) {
        FluidState fluidState = this.fluidStateContainer.get(x, y, z);
        if (fluidState.isEmpty()) {
            cir.setReturnValue(this.getBlockStateContainer().get(x, y, z).getFluidState());
        } else {
            cir.setReturnValue(fluidState);
        }
        cir.cancel();
    }

    @Override
    public FluidState setFluidStateLocally(int x, int y, int z, FluidState state) {
        return this.setFluidState(x, y, z, state, true, true);
    }

    @Unique
    private FluidState setFluidState(int x, int y, int z, FluidState state, boolean lock, boolean counts){
        FluidState oldState = this.fluidStateContainer.get(x, y, z);
        if (lock) {
            this.fluidStateContainer.swap(x, y, z, state);
        }
        else {
            this.fluidStateContainer.swapUnsafe(x, y, z, state);
        }
        if (counts){
            if (!oldState.isEmpty() && oldState.hasRandomTicks()) {
                this.nonEmptyFluidCount -= 1;
            }
            if (!state.isEmpty() && state.hasRandomTicks()) {
                this.nonEmptyFluidCount += 1;
            }
        }
        return oldState;
    }

    @Inject(method = "getPacketSize", at = @At("RETURN"), cancellable = true)
    private void getNewPacketSize(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(cir.getReturnValue() + this.fluidStateContainer.getPacketSize() + 1);
        cir.cancel();
    }

    @Inject(method = "toPacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/PalettedContainer;writePacket(Lnet/minecraft/network/PacketByteBuf;)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void modifyToPacket(PacketByteBuf buf, CallbackInfo ci){
        buf.writeShort(this.nonEmptyFluidCount);
        this.fluidStateContainer.writePacket(buf);
    }

    @Inject(method = "readDataPacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/PalettedContainer;readPacket(Lnet/minecraft/network/PacketByteBuf;)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void modifyReadPacket(PacketByteBuf buf, CallbackInfo ci){
        this.nonEmptyFluidCount = buf.readShort();
        this.fluidStateContainer.readPacket(buf);
    }

    @Inject(method = "lock", at = @At("TAIL"))
    private void lockFluidStateContainer(CallbackInfo ci){
        this.fluidStateContainer.lock();
    }

    @Inject(method = "unlock", at = @At("TAIL"))
    private void unlockFluidStateContainer(CallbackInfo ci){
        this.fluidStateContainer.unlock();
    }

    @Inject(method = "isEmpty", at = @At("RETURN"), cancellable = true)
    private void judgeEmpty(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(cir.getReturnValue() && this.nonEmptyFluidCount == 0);
        cir.cancel();
    }

//  @WrapOperation(method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;swap(IIILjava/lang/Object;)Ljava/lang/Object;"))
//    private <T> T preventSetFluidBlockStateSafe(PalettedContainer<T> instance, int x, int y, int z, T value, Operation<T> original){
//        BlockState state = (BlockState) value;
//        BlockState newState = modifyStateBeforeSwap(x, y, z, state, true);
//        return instance.swap(x, y, z, (T) newState);
//    }
//
//    @WrapOperation(method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;swapUnsafe(IIILjava/lang/Object;)Ljava/lang/Object;"))
//    private <T> T preventSetFluidBlockStateUnsafe(PalettedContainer<T> instance, int x, int y, int z, T value, Operation<T> original){
//        BlockState state = (BlockState) value;
//        BlockState newState = modifyStateBeforeSwap(x, y, z, state, false);
//        return instance.swapUnsafe(x, y, z, (T) newState);
//    }
//
//    private BlockState modifyStateBeforeSwap(int x, int y, int z, BlockState state, boolean lock){
//        if (state.isAir()){
//            this.setFluidState(x, y, z, Fluids.EMPTY.getDefaultState(), lock, false);
//            return state;
//        }
//        else if (state.getBlock() instanceof FluidBlock){
//            this.setFluidState(x, y, z, state.getFluidState(), lock, false);
//            return Blocks.AIR.getDefaultState();
//        }
//        else if (state.getBlock() instanceof Waterloggable){
//            if (state.get(Properties.WATERLOGGED)){
//                this.setFluidState(x, y, z, Fluids.WATER.getDefaultState(), lock, false);
//            }
//            else {
//                this.setFluidState(x, y, z, Fluids.EMPTY.getDefaultState(), lock, false);
//            }
//            return state;
//        }
//        else {
//            return state;
//        }
//    }
}
