package name.synchro.networkLink.networkBlockAPI;

import name.synchro.networkLink.networkAlgorithm.NetworkLinkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

import static name.synchro.SynchroStandardStatic.DIRECTION_PROPERTY_MAP;

public abstract class AbstractNetworkBlock extends Block implements NetworkSearchProvider {
    public AbstractNetworkBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(Properties.EAST,false)
                .with(Properties.NORTH,false)
                .with(Properties.WEST,false)
                .with(Properties.SOUTH,false)
                .with(Properties.UP,false)
                .with(Properties.DOWN,false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.EAST,Properties.NORTH,Properties.WEST,
                Properties.SOUTH,Properties.UP,Properties.DOWN);
    }

    @Override
    public abstract HashSet<Direction> getLinkableDirections(BlockState state);
    public boolean isDirectionLinkable(Direction direction, BlockState state){
        return getLinkableDirections(state).contains(direction);
    };
    public HashSet<Direction> getLinkedDirections(BlockState state) {
        HashSet<Direction> linkedDirections = new HashSet<>();
        for (Direction direction: getLinkableDirections(state)){
            if (state.get(DIRECTION_PROPERTY_MAP.get(direction))){
                linkedDirections.add(direction);
            }
        }
        return linkedDirections;
    }

    /**
     * Should be called in method: onBlockAdded(...)
     */
    private HashSet<Direction> autoLink(BlockState state, World world, BlockPos pos){
        HashSet<Direction> linkedDirections = new HashSet<>();
        for (Direction direction: this.getLinkableDirections(state)){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if(neighborState.getBlock() instanceof AbstractNetworkBlock neighborBlock){
                if (neighborBlock.isDirectionLinkable(direction.getOpposite(), neighborState)) {
                    state = state.with(DIRECTION_PROPERTY_MAP.get(direction), true);
                    linkedDirections.add(direction);
                    world.setBlockState(neighborPos, neighborState
                            .with(DIRECTION_PROPERTY_MAP.get(direction.getOpposite()), true));
                }
            }

        }
        world.setBlockState(pos,state);
        return linkedDirections;
    }

    /**
     * Should be called in method: onStateReplaced(...)
     */
    private HashSet<Direction> autoCut(BlockState state, World world, BlockPos pos){
        HashSet<Direction> cutDirections = new HashSet<>();
        for (Direction direction: this.getLinkableDirections(state)){
            BlockPos neighborPos = pos.offset(direction);
            if (world.getBlockState(neighborPos).getBlock() instanceof AbstractNetworkBlock){
                cutDirections.add(direction);
                world.setBlockState(neighborPos,
                        world.getBlockState(neighborPos)
                                .with(DIRECTION_PROPERTY_MAP.get(direction.getOpposite()),false));
            }
        }
        return cutDirections;
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() != state.getBlock()) {
            NetworkLinkManager.linkUpActions(world, pos, state, autoLink(state,world,pos));
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(!state.isOf(newState.getBlock())){
            autoCut(state,world,pos);
            NetworkLinkManager.cutOffActions(world, pos, state, autoCut(state,world,pos));
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
