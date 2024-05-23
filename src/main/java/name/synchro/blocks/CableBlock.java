package name.synchro.blocks;

import name.synchro.electricNetwork.ElectricConductorBlockProvider;
import name.synchro.networkLink.networkBlockAPI.AbstractNetworkBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Arrays;
import java.util.HashSet;

public class CableBlock extends AbstractNetworkBlock implements ElectricConductorBlockProvider {
    public static final VoxelShape NODE_CUBE = VoxelShapes.cuboid(4/16f,4/16f,4/16f,12/16f,12/16f,12/16f);
    public static final VoxelShape EAST_CUBE = VoxelShapes.cuboid(8/16f,4/16f,4/16f,16/16f,12/16f,12/16f);
    public static final VoxelShape WEST_CUBE = VoxelShapes.cuboid(0/16f,4/16f,4/16f,8/16f,12/16f,12/16f);
    public static final VoxelShape NORTH_CUBE = VoxelShapes.cuboid(4/16f,4/16f,0/16f,12/16f,12/16f,8/16f);
    public static final VoxelShape SOUTH_CUBE = VoxelShapes.cuboid(4/16f,4/16f,8/16f,12/16f,12/16f,16/16f);
    public static final VoxelShape UP_CUBE = VoxelShapes.cuboid(4/16f,8/16f,4/16f,12/16f,16/16f,12/16f);
    public static final VoxelShape DOWN_CUBE = VoxelShapes.cuboid(4/16f,0/16f,4/16f,12/16f,8/16f,12/16f);
    public CableBlock(Settings settings) {
        super(settings);

    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape result = NODE_CUBE;
        if(state.get(Properties.EAST)) result=VoxelShapes.union(result,EAST_CUBE);
        if(state.get(Properties.WEST)) result=VoxelShapes.union(result,WEST_CUBE);
        if(state.get(Properties.NORTH)) result=VoxelShapes.union(result,NORTH_CUBE);
        if(state.get(Properties.SOUTH)) result=VoxelShapes.union(result,SOUTH_CUBE);
        if(state.get(Properties.UP)) result=VoxelShapes.union(result,UP_CUBE);
        if(state.get(Properties.DOWN)) result=VoxelShapes.union(result,DOWN_CUBE);
        return result;
    }

    @Override
    public HashSet<Direction> getLinkableDirections(BlockState state) {
        return new HashSet<>(Arrays.stream(Direction.values()).toList());
    }

}
