package name.synchro.fluids.gases;

import name.synchro.fluids.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.joml.Vector3f;

public abstract class Gas extends Fluid {
    public static final IntProperty LEVEL = IntProperty.of("concentration_level", 1, 16);
    public static final int MAX_LEVEL = 16;
    public final int upsideMinGradient;
    public final int downsideMinGradient;
    public final int horizontalMinGradient;
    public final int volatilizationRate;

    public final int color;
    public final Vector3f colorVector;
    protected final DustParticleEffect gasParticleEffect;
    public Gas(int color, int upsideMinGradient, int downsideMinGradient, int horizontalMinGradient, int volatilizationRate) {
        this.color = color;
        this.colorVector = new Vector3f((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f);
        this.gasParticleEffect = new DustParticleEffect(colorVector, 0.5f);
        this.upsideMinGradient = Math.max(upsideMinGradient, 2) ;
        this.downsideMinGradient = Math.max(downsideMinGradient, 2);
        this.horizontalMinGradient = Math.max(horizontalMinGradient, 2);
        this.volatilizationRate = volatilizationRate;
        setDefaultState(getDefaultState().with(LEVEL, MAX_LEVEL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
        tryDiffuse(world,pos,state);
    }

    protected void tryDiffuse(World world, BlockPos pos, FluidState state){
        int thisLevel = state.getLevel();
        int thisLevelAfter = thisLevel;
        for (Direction direction: Direction.values()){
            BlockPos neighborPos = pos.offset(direction);
            FluidState neighborFluidState = world.getFluidState(neighborPos);
            int neighborLevel;
            if (world.getBlockState(neighborPos).isAir()){
                neighborLevel = 0;
            }
            else if (neighborFluidState.isOf(state.getFluid())){
                neighborLevel = neighborFluidState.getLevel();
            }
            else continue;
            thisLevelAfter = Math.min(thisLevelAfter, switch (direction){
                case UP ->  diffuse(world, state, neighborPos, thisLevel, neighborLevel, upsideMinGradient);
                case DOWN -> diffuse(world, state, neighborPos, thisLevel, neighborLevel, downsideMinGradient);
                default -> diffuse(world, state, neighborPos, thisLevel, neighborLevel, horizontalMinGradient);
            });
        }
        ((FluidHelper.ForWorld) world).setFluidState(pos, state.with(LEVEL, thisLevelAfter));
    }

    private int diffuse(World world, FluidState fluidState, BlockPos neighborPos, int thisLevel, int neighborLevel, int minGradient){
        int gradient = thisLevel - neighborLevel;
        if (gradient >= minGradient){
            int totalLevel = thisLevel + neighborLevel;
            int neighborAfterLevel = (totalLevel + 2 - minGradient) / 2;
            ((FluidHelper.ForWorld) world).setFluidState(neighborPos, fluidState.with(LEVEL, neighborAfterLevel));
            return totalLevel - neighborAfterLevel;
        }
        else return thisLevel;
    }

    @Override
    public Item getBucketItem() {
        return Items.BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        return Vec3d.ZERO;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 2;
    }

    @Override
    protected final float getBlastResistance() {
        return 100f;
    }

    @Override
    public final float getHeight(FluidState state, BlockView world, BlockPos pos) {
        return 1f;
    }

    @Override
    public final float getHeight(FluidState state) {
        return 1f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public final boolean isStill(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState state) {
        return state.get(LEVEL);
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        volatilizeGas(world, pos, state, random);
    }

    protected void volatilizeGas(World world, BlockPos pos, FluidState state, Random random){
        int thisLevel = state.getLevel();
        int facesUnexposed = 6;
        for (Direction direction: Direction.values()){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborBlockState = world.getBlockState(neighborPos);
            FluidState neighborFluidState = world.getFluidState(neighborPos);
            if (neighborBlockState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite())
            || neighborFluidState.isOf(state.getFluid())) facesUnexposed--;
        }
        if (random.nextInt(6400) < (volatilizationRate << facesUnexposed)){
            if (thisLevel > 1){
                ((FluidHelper.ForWorld) world).setFluidState(pos, state.with(LEVEL,thisLevel - 1));
            }
            else {
                ((FluidHelper.ForWorld) world).setFluidState(pos, Fluids.EMPTY.getDefaultState());
                if (world instanceof ServerWorld serverWorld){
                    serverWorld.spawnParticles(
                            gasParticleEffect, pos.getX(), pos.getY(), pos.getZ(),
                            random.nextInt(6), random.nextDouble(), random.nextDouble(), random.nextDouble(), 0);
                }
            }

        }
    }

}
