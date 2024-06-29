package name.synchro.mixin.FluidConcerned;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import name.synchro.fluids.FluidHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Queue;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin extends Block{
    public SpongeBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "absorbWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean testForWater(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original,
                                 @Local(argsOnly = true)World world, @Local Queue<Pair<BlockPos, Integer>> queue,
                                 @Local(ordinal = 1)int distance, @Local BlockState blockState,
                                 @Local FluidState fluidState, @Local(ordinal = 2) BlockPos blockPos2,
                                 @Local(ordinal = 0) LocalIntRef absorbedAmount){
        if (original.call(instance, tag)) {
            ((FluidHelper.ForWorld) (world)).synchro$setFluidState(blockPos2, Fluids.EMPTY.getDefaultState(), Block.NOTIFY_ALL, 512);
            absorbedAmount.set(absorbedAmount.get() + 1);
            if (distance < 6) {
                queue.add(new Pair<>(blockPos2, distance + 1));
            }
            if (material == Material.UNDERWATER_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                dropStacks(blockState, world, blockPos2, blockEntity);
                world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
        return false;
    }
}
