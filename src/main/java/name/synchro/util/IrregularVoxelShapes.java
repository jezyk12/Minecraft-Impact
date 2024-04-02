package name.synchro.util;

import name.synchro.Synchro;
import name.synchro.blockEntities.MillstoneBlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashMap;
import java.util.Map;

public class IrregularVoxelShapes {
    /**
     * Defines how many parts will be split to from 1-block-length when fitting irregular voxel shape with small unit cubes.
     * <p>Rising this number can increase the quality of irregular voxel shapes but could cause serious lag. Recommended: 16</p>
     */
    private static final int SLICES = 32;
    /**
     * All voxel shapes should be preloaded and stored in this static map or else that will cause serious lag.
     */
    protected static final Map<String, VoxelShape> LOADED_SHAPES = new HashMap<>();

    public static void addAndLoadAllShapes(){
        Synchro.LOGGER.info("Loading all complex voxel shapes...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 180; i += 1){
            IrregularVoxelShapes.addShape(MillstoneBlockEntity.SHAPE_KEY_PREFIX + i, IrregularVoxelShapes.createRotatedCube(Direction.Axis.Y, 7 / 16d, 1d, 7 / 16d, i));
        }
        Synchro.LOGGER.info("All complex voxel shapes have been loaded. Took " + (System.currentTimeMillis() - start) + " ms.");
    }

    protected static void addShape(String name, VoxelShape shape){
        LOADED_SHAPES.put(name, shape);
    }

    public static VoxelShape getShape(String name){
        return LOADED_SHAPES.get(name);
    }

    protected static VoxelShape createRotatedCube(Direction.Axis axis, double thickness, double iLength, double jLength, int degree){
        if (degree % 90 == 0){
            double i, j;
            if (degree % 180 == 0) {
                i = iLength / 2;
                j = jLength / 2;
            }
            else {
                i = jLength / 2;
                j = iLength / 2;
            }
            return VoxelShapes.cuboid(switch(axis){
                case X -> new Box(thickness / -2, -j, -i, thickness / 2, j, i);
                case Y -> new Box(-j, thickness / -2, -i, j, thickness / 2, i);
                case Z -> new Box(-i, -j, thickness / -2, i, j, thickness / 2);
            });
        }
        float radians = (float) Math.toRadians(degree % 180);
        Vec3d v1 = new Vec3d(iLength / 2, 0, jLength / 2).rotateY(radians);
        Vec3d v2 = new Vec3d(iLength / -2, 0, jLength / 2).rotateY(radians);
        double i0 = v1.getX();
        double j0 = v1.getZ();
        double i1 = v2.getX();
        double j1 = v2.getZ();
        PointIJ p0 = new PointIJ(i0, j0);
        PointIJ p1 = new PointIJ(-i0, -j0);
        PointIJ p2 = new PointIJ(i1, j1);
        PointIJ p3 = new PointIJ(-i1, -j1);
        PointIJ iMin = getMinIPoint(p0, p1, p2, p3);
        PointIJ iMax = getMaxIPoint(p0, p1, p2, p3);
        PointIJ jMin = getMinJPoint(p0, p1, p2, p3);
        PointIJ jMax = getMaxJPoint(p0, p1, p2, p3);
        int iSlices = (int) Math.ceil ((iMax.i() - iMin.i()) * SLICES);
        int jSlices = (int) Math.ceil ((jMax.j() - jMin.j()) * SLICES);
        VoxelShape shapeI = VoxelShapes.empty();
        for (int i = 0; i < iSlices; i++) {
            double iPos = iMin.i() + (iMax.i() - iMin.i()) * (i + 0.5) / iSlices;
            double jMinForI = getMinJForGivenI(iMin, jMin, jMin, iMax, iPos);
            double jMaxForI = getMaxJForGivenI(iMin, jMax, jMax, iMax, iPos);
            double di = (iMax.i() - iMin.i()) * 0.5 / iSlices;
            VoxelShape part = VoxelShapes.cuboid(
                    switch (axis){
                        case X -> new Box(thickness / -2, jMinForI, iPos - di, thickness / 2, jMaxForI, iPos + di);
                        case Y -> new Box(jMinForI, thickness / -2, iPos - di, jMaxForI, thickness / 2, iPos + di);
                        case Z -> new Box(iPos - di, jMinForI, thickness / -2, iPos + di, jMaxForI, thickness / 2);
                    }
            );
            shapeI = VoxelShapes.union(shapeI, part);
        }
        VoxelShape shapeJ = VoxelShapes.empty();
        for (int j = 0; j < jSlices; j++) {
            double jPos = jMin.j() + (jMax.j() - jMin.j()) * (j + 0.5) / jSlices;
            double iMinForJ = getMinIForGivenJ(jMin, iMin, iMin, jMax, jPos);
            double iMaxForJ = getMaxIForGivenJ(jMin, iMax, iMax, jMax, jPos);
            double dj = (jMax.j() - jMin.j()) * 0.5 / jSlices;
            VoxelShape part = VoxelShapes.cuboid(
                    switch (axis){
                        case X -> new Box(thickness / -2, jPos - dj, iMinForJ, thickness / 2, jPos + dj, iMaxForJ);
                        case Y -> new Box(jPos - dj, thickness / -2, iMinForJ, jPos + dj, thickness / 2, iMaxForJ);
                        case Z -> new Box(iMinForJ, jPos - dj, thickness / -2, iMaxForJ, jPos + dj, thickness / 2);
                    }
            );
            shapeJ = VoxelShapes.union(shapeJ, part);
        }
        return VoxelShapes.combineAndSimplify(shapeI, shapeJ, BooleanBiFunction.AND);
    }

    private record PointIJ(double i, double j){}
    private static PointIJ getMaxIPoint(PointIJ... points){
        PointIJ max = points[0];
        for (PointIJ point : points){
            if (point.i() > max.i()){
                max = point;
            }
        }
        return max;
    }
    private static PointIJ getMinIPoint(PointIJ... points){
        PointIJ min = points[0];
        for (PointIJ point : points){
            if (point.i() < min.i()){
                min = point;
            }
        }
        return min;
    }
    private static PointIJ getMaxJPoint(PointIJ... points){
        PointIJ max = points[0];
        for (PointIJ point : points){
            if (point.j() > max.j()){
                max = point;
            }
        }
        return max;
    }
    private static PointIJ getMinJPoint(PointIJ... points){
        PointIJ min = points[0];
        for (PointIJ point : points){
            if (point.j() < min.j()){
                min = point;
            }
        }
        return min;
    }
    private static double getMinJForGivenI(PointIJ p00, PointIJ p01, PointIJ p10, PointIJ p11, double i){
        double j00 = p00.j();
        double j01 = p01.j();
        double j10 = p10.j();
        double j11 = p11.j();
        double jl0 = j00 + (j01 - j00) * (i - p00.i()) / (p01.i() - p00.i());
        double jl1 = j10 + (j11 - j10) * (i - p10.i()) / (p11.i() - p10.i());
        return Math.max(jl0, jl1);
    }

    private static double getMaxJForGivenI(PointIJ p00, PointIJ p01, PointIJ p10, PointIJ p11, double i){
        double j00 = p00.j();
        double j01 = p01.j();
        double j10 = p10.j();
        double j11 = p11.j();
        double jl0 = j00 + (j01 - j00) * (i - p00.i()) / (p01.i() - p00.i());
        double jl1 = j10 + (j11 - j10) * (i - p10.i()) / (p11.i() - p10.i());
        return Math.min(jl0, jl1);
    }

    private static double getMinIForGivenJ(PointIJ p00, PointIJ p01, PointIJ p10, PointIJ p11, double j){
        double i00 = p00.i();
        double i01 = p01.i();
        double i10 = p10.i();
        double i11 = p11.i();
        double il0 = i00 + (i01 - i00) * (j - p00.j()) / (p01.j() - p00.j());
        double il1 = i10 + (i11 - i10) * (j - p10.j()) / (p11.j() - p10.j());
        return Math.max(il0, il1);
    }

    private static double getMaxIForGivenJ(PointIJ p00, PointIJ p01, PointIJ p10, PointIJ p11, double j){
        double i00 = p00.i();
        double i01 = p01.i();
        double i10 = p10.i();
        double i11 = p11.i();
        double il0 = i00 + (i01 - i00) * (j - p00.j()) / (p01.j() - p00.j());
        double il1 = i10 + (i11 - i10) * (j - p10.j()) / (p11.j() - p10.j());
        return Math.min(il0, il1);
    }




}
