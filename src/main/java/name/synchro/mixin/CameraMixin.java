package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.fluids.gases.Gas;
import name.synchro.mixinHelper.CameraDuck;
import name.synchro.mixinHelper.CameraGasSubmersionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin implements CameraDuck {
    @Shadow
    private BlockView area;
    @Shadow
    private boolean ready;
    @Final
    @Shadow
    private BlockPos.Mutable blockPos;
    @Shadow
    private Vec3d pos;

    @WrapOperation(method = "getSubmersionType",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 0))
    private boolean getSubmersionTypeMixin(BlockState instance, Block block, Operation<Boolean> original){
//        if (instance.getBlock() instanceof GasBlock){
//            return true;
//        }
//        else
        return original.call(instance, block);
    }

    @Override
    @Unique
    public CameraGasSubmersionType getGasSubmersionType(){
        if (!this.ready) {
            return CameraGasSubmersionType.NONE;
        }
        FluidState fluidState = this.area.getFluidState(this.blockPos);
        if (fluidState.getFluid() instanceof Gas && this.pos.y < (double)(this.blockPos.getY() + 1)) {
            int level = fluidState.getLevel();
            return CameraGasSubmersionType.values()[(level + 3) / 4];
        }
        return CameraGasSubmersionType.NONE;
    }

}
