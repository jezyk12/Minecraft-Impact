package name.synchro.networkLink.networkAlgorithm;

import name.synchro.networkLink.networkBlockAPI.NetworkSearchProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class NetworkSearchHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(NetworkSearchHandler.class);
    static HashSet<BlockPos> startDFS(World world, BlockPos startPos){
        return doDFS(world,startPos, new HashSet<BlockPos>(),true);
    }
    static HashSet<BlockPos> doDFS(World world, BlockPos pos, HashSet<BlockPos> metBlocks, Boolean isStartBlock){
        if(metBlocks.add(pos)){     //if never search this block
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof NetworkSearchProvider block) {
                if (isStartBlock|| block.isNotDeadEnd()) {     //if it'd be looked as a dead end or not
                    HashSet<Direction> linkedDirections = block.getLinkedDirections(state);
                    for (Direction direction : linkedDirections) {
                        if (world.getBlockState(pos.offset(direction)).getBlock() instanceof NetworkSearchProvider) {
                            metBlocks.addAll(doDFS(world,pos.offset(direction),metBlocks,false));
                        }
                    }
                }
            }
            else {
                outputErrorMsg(pos);
            }
        }
        return metBlocks;
    }
    static HashSet<BlockPos> startBFS(World world, BlockPos startPos){
        //metBlocks.remove(startPos);
        return doBFS(world,startPos, new HashSet<>(), new HashSet<>());
    }
    static HashSet<BlockPos> doBFS(World world, BlockPos startPos, HashSet<BlockPos> metBlocks, HashSet<BlockPos> nextBlocks){
        nextBlocks.add(startPos);
        while (!nextBlocks.isEmpty()){
            HashSet<BlockPos> searchingBlocks = new HashSet<>(nextBlocks);
            nextBlocks.clear();
            for (BlockPos pos: searchingBlocks){
                if(metBlocks.add(pos)){     //if never search this block
                    BlockState state = world.getBlockState(pos);
                    if ((state.getBlock() instanceof NetworkSearchProvider block)) {
                        if ((pos==startPos)|| block.isNotDeadEnd()) {     //if it'd be looked as a dead end or not
                            HashSet<Direction> linkedDirections = block.getLinkedDirections(state);
                            for (Direction direction : linkedDirections) {
                                if (world.getBlockState(pos.offset(direction)).getBlock() instanceof NetworkSearchProvider) {
                                    nextBlocks.add(pos.offset(direction));
                                }
                            }
                        }
                    }
                    else {
                        outputErrorMsg(pos);
                    }
                }
            }
        }
        return metBlocks;
    }
    private static void outputErrorMsg(BlockPos pos) {
        LOGGER.warn("Network search went wrong at "+pos);
    }
    public static void loadNetworkSearchHandler(){
        LOGGER.info("Network Search Handler has been initialized.");
    }
}

