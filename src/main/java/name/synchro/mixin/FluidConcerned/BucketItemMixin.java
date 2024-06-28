package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item implements FluidHelper.ForBucketItem {

    @Shadow @Final private Fluid fluid;

    @Shadow public abstract boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult);

    @Shadow protected abstract void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos);

    public BucketItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void onBucketItemUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir,
                                 @Local BlockHitResult blockHitResult) {
        FluidUtil.onBucketItemUse(((BucketItem)(Object)this), world, user, hand, blockHitResult).ifPresent(result -> {
            cir.setReturnValue(result);
            cir.cancel();
        });
    }

    @Inject(method = "placeFluid", at = @At("HEAD"), cancellable = true)
    private void onPlacedFluid(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir){
        FluidUtil.onBucketItemPlacedFluid(((BucketItem)(Object)this), player, world, pos, hitResult).ifPresent(result -> {
            cir.setReturnValue(result);
            cir.cancel();
        });
    }

    @Override
    public Fluid synchro$getFluid() {
        return this.fluid;
    }

    @Override
    public void synchro$callPlayEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        this.playEmptyingSound(player, world, pos);
    }
}

