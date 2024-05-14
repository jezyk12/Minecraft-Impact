package name.synchro.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BananaBlock extends Block implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_3;
    private static final VoxelShape[] SHAPES = {
            VoxelShapes.cuboid( 5/16d, 5/16d, 5/16d, 11/16d, 16/16d, 11/16d),
            VoxelShapes.cuboid( 4/16d, 4/16d, 4/16d, 12/16d, 16/16d, 12/16d),
            VoxelShapes.cuboid( 3/16d, 3/16d, 3/16d, 13/16d, 16/16d, 13/16d),
            VoxelShapes.cuboid( 2/16d, 2/16d, 2/16d, 14/16d, 16/16d, 14/16d)
    };

    public BananaBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AGE, 3));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return state.get(AGE) < 3;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0){
            this.grow(world, random, pos, state);
        }
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Integer age = state.get(AGE);
        if (age < 3) world.setBlockState(pos, state.with(AGE, age + 1));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(AGE)];
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(AGE)];
    }
}
