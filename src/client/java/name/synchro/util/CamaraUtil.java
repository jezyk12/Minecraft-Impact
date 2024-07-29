package name.synchro.util;

import name.synchro.fluids.gases.Gas;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public final class CamaraUtil {
    public static final double S2D2 = Math.sqrt(2) / 2;

    public static int getGasOverlayColor(BlockView world, Camera camera) {
        BlockPos blockPos = camera.getBlockPos();
        if (world != null && world.getFluidState(blockPos).getFluid() instanceof Gas gas) {
            float concentration = (float) world.getFluidState(blockPos).getLevel() / Gas.MAX_LEVEL;
            float factor = getViewingInfluenceFactor(world, camera);
            float opaquePercent = concentration * factor * 0.5f + 0.05f;
            int alpha =  (int) (Math.min(Math.max(opaquePercent, 0f), 1f)  * 255f) << 24;
            return (gas.color & 0xffffff) | alpha;
        }
        else return -1;
    }

    private static float getViewingInfluenceFactor(BlockView world, Camera camera){
        Vec3d pos = camera.getPos();
        Vec3d centerPos = camera.getBlockPos().toCenterPos();
        double distance = pos.distanceTo(centerPos);
        Vec3d posVec = pos.subtract(centerPos);
        Vec3d towards = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
        Direction direction = Direction.getFacing(towards.x, towards.y, towards.z);
        BlockPos neighborBlockPos = camera.getBlockPos().offset(direction);
        Vec3d toNeighborVec = neighborBlockPos.toCenterPos().subtract(centerPos);
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);
        FluidState neighborFluidState = world.getFluidState(neighborBlockPos);
        if (neighborBlockState.isSideSolidFullSquare(world, neighborBlockPos, direction.getOpposite())
                || neighborFluidState.isOf(world.getFluidState(camera.getBlockPos()).getFluid())) {
            double cosPhi = toNeighborVec.normalize().dotProduct(towards.normalize());
            double when45d = function0(S2D2, distance);
            return (float) function1(cosPhi, when45d);
        }
        else{
            double cosTheta = posVec.normalize().dotProduct(towards.normalize());
            return (float) function0(cosTheta, distance);
        }
    }

    private static double function0(double cosTheta, double distance) {
        return -cosTheta * Math.min(distance / 0.5f, 1.0f) / 2 + 0.5;
    }

    private static double function1(double cosPhi, double when45d) {
        return (Math.max(cosPhi, S2D2) - 1) * (when45d - 1) / (S2D2 - 1) + 1;
    }

    public interface GasSubmersionProvider {
        GasSubmersionType synchro$getGasSubmersionType();

    }

    public enum GasSubmersionType {
        NONE,
        GAS_LITTLE,
        GAS_THIN,
        GAS_NORMAL,
        GAS_THICK
    }
}
