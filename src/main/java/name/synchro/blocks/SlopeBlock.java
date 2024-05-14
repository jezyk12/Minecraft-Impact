package name.synchro.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SlopeBlock extends StairsBlock {
    public static final Set<SlopeBlock> SLOPE_BLOCKS = new HashSet<>();
    private static final int SLOPE_PART_AMOUNT = 16;
    private static final HashMap<SlopeState, VoxelShape> SLOPE_SHAPES;
    static {
        SLOPE_SHAPES = new HashMap<>();
        for (Direction direction: Direction.Type.HORIZONTAL){
            for (StairShape stairShape: StairShape.values()){
                SLOPE_SHAPES.put(new SlopeState(direction, false, stairShape),
                        generateSlopeShape(direction, false, stairShape));
                SLOPE_SHAPES.put(new SlopeState(direction, true, stairShape),
                        generateSlopeShape(direction, true, stairShape));
            }
        }
    }
    private record SlopeState(Direction direction, boolean flip, StairShape stairShape){
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SlopeState that = (SlopeState) o;
            return flip == that.flip && direction == that.direction && stairShape == that.stairShape;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, flip, stairShape);
        }
    }

    public SlopeBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings.hardness(baseBlockState.getBlock().getHardness())
                .resistance(baseBlockState.getBlock().getBlastResistance())
                .sounds(baseBlockState.getSoundGroup())
                .nonOpaque());
        SLOPE_BLOCKS.add(this);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlopeShape(state.get(FACING), state.get(HALF).equals(BlockHalf.TOP), state.get(SHAPE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape(state, world, pos, context);
    }

    private static VoxelShape getSlopeShape(Direction direction, boolean flip, StairShape stairShape){
        return SLOPE_SHAPES.get(new SlopeState(direction, flip, stairShape));
    }

    public static VoxelShape generateSlopeShape(Direction direction, boolean flip, StairShape stairShape){
        VoxelShape shape = VoxelShapes.empty();
        int degree = switch (direction) {
            case SOUTH -> 0;
            case EAST -> 90;
            case NORTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        for (int i = 0; i < SLOPE_PART_AMOUNT; i++) {
            final double bigPosBiggerPlus = (double) (i + 1) / SLOPE_PART_AMOUNT;
            final double smallPosBigger = 1 - bigPosBiggerPlus;
            final double bigPosBigger = (double) i / SLOPE_PART_AMOUNT;
            final double smallPosBiggerPlus = 1 - bigPosBigger;
            final double minPos = 0d;
            final double maxPos = 1d;
            VoxelShape part;
            switch (stairShape) {
                default -> {
                    Box box = new Box(minPos, minPos, smallPosBigger, maxPos, smallPosBiggerPlus, smallPosBiggerPlus);
                    part = VoxelShapes.cuboid(transformBox(box, degree, flip));
                }
                case INNER_LEFT, INNER_RIGHT-> {
                    Box boxToBeSub = transformBox(new Box(minPos, bigPosBigger, minPos, bigPosBigger, bigPosBiggerPlus, bigPosBigger),
                            degree - (stairShape.equals(StairShape.INNER_LEFT) ? 0 : 90), flip);
                    VoxelShape shapeToBeSub = VoxelShapes.cuboid(boxToBeSub);
                    VoxelShape shapeToSub = VoxelShapes.cuboid(minPos, bigPosBigger, minPos, maxPos, bigPosBiggerPlus, maxPos);
                    part = VoxelShapes.combineAndSimplify(shapeToSub, shapeToBeSub, (a, b) -> a && !b);
                }
                case OUTER_LEFT, OUTER_RIGHT ->{
                    Box box = new Box(bigPosBigger, bigPosBigger, bigPosBigger, maxPos, bigPosBiggerPlus, maxPos);
                    part = VoxelShapes.cuboid(transformBox(box, degree - (stairShape.equals(StairShape.OUTER_LEFT) ? 0 : 90), flip));
                }
            }
            shape = VoxelShapes.combineAndSimplify(shape, part, (a, b) -> a || b);
        }
        return shape;
    }

    private static Box transformBox(Box origin, int degree, boolean flip){
        Vec3d minPos = new Vec3d(origin.minX, origin.minY, origin.minZ).subtract(0.5, 0.5, 0.5);
        Vec3d maxPos = new Vec3d(origin.maxX, origin.maxY, origin.maxZ).subtract(0.5, 0.5, 0.5);
        Vec3d newVec0 = minPos.rotateY((float) (degree / 180d * Math.PI)).add(0.5, 0.5, 0.5).withAxis(Direction.Axis.Y, flip ? 1d - origin.minY : origin.minY);
        Vec3d newVec1 = maxPos.rotateY((float) (degree / 180d * Math.PI)).add(0.5, 0.5, 0.5).withAxis(Direction.Axis.Y, flip ? 1d - origin.maxY : origin.maxY);
        return new Box(newVec0, newVec1);
    }

}
