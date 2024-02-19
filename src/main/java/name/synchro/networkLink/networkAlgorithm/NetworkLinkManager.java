package name.synchro.networkLink.networkAlgorithm;

import name.synchro.networkLink.networkBlockAPI.NetworkSearchProvider;
import name.synchro.networkLink.networkBlockEntityAPI.AbstractNetworkBlockEntity;
import name.synchro.networkLink.networkBlockEntityAPI.NetworkBlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

public class NetworkLinkManager {
    public static HashSet<BlockPos> filtrateTerminals(HashSet<BlockPos> blocks, World world){
        HashSet<BlockPos> terminals = new HashSet<>();
        for (BlockPos blockPos: blocks){
            BlockEntity blockEntity =  world.getBlockEntity(blockPos);
            if (blockEntity!=null){
                if (blockEntity instanceof AbstractNetworkBlockEntity){
                    terminals.add(blockPos);
                }
            }
        }
        return terminals;
    }
    public static void linkUpActions(World world, BlockPos pos, BlockState state,HashSet<Direction> linkedDirections){
        if (!state.hasBlockEntity()) {
            if (linkedDirections.size() > 1) {
                HashSet<BlockPos> linkedTerminals =
                        filtrateTerminals(NetworkSearchHandler.startBFS(world, pos), world);
                allLinkUp(linkedTerminals,world);
            }
        }
        else {
            if (!linkedDirections.isEmpty()) {
                HashSet<BlockPos> linkedTerminals =
                        filtrateTerminals(NetworkSearchHandler.startDFS(world, pos), world);
                linkMeUp(linkedTerminals,world,pos);
                selfLinkAll(linkedTerminals,world,pos);
            }
        }
    }

    public static void cutOffActions(World world, BlockPos pos, BlockState state, HashSet<Direction> linkedDirections){
        if (!state.hasBlockEntity()) {
            if (linkedDirections.size() > 1) {
                HashSet<BlockPos> influencedTerminals = new HashSet<>();
                for (Direction direction: linkedDirections) {
                    BlockPos neighborPos = pos.offset(direction);
                    influencedTerminals.addAll(
                            filtrateTerminals(NetworkSearchHandler.startBFS(world, neighborPos), world));
                }
                allRelink(influencedTerminals, world);
            }
        }
        else {
            if (!linkedDirections.isEmpty()) {
                HashSet<BlockPos> influencedTerminals = new HashSet<>();
                for (Direction direction: linkedDirections) {
                    BlockPos neighborPos = pos.offset(direction);
                    influencedTerminals.addAll(
                            filtrateTerminals(NetworkSearchHandler.startDFS(world, neighborPos), world));
                }
                cutMeOff(influencedTerminals, world, pos);
            }
        }
    }
    public static boolean allRelink(HashSet<BlockPos> terminals, World world){
        for (BlockPos executor : terminals) {
            if(world.getBlockEntity(executor) instanceof NetworkBlockEntityProvider blockEntity) {
                BlockState state = world.getBlockState(executor);
                if (state.getBlock() instanceof NetworkSearchProvider block) {
                    HashSet<Direction> linkedDirections = block.getLinkedDirections(state);
                    blockEntity.clearLinkState();
                    linkUpActions(world,executor,state,linkedDirections);
                }
                blockEntity.onLinkStateChanged();
            }
        }
        return true;
    }
    public static boolean allLinkUp(HashSet<BlockPos> terminals, World world){
        boolean executed = false;
        for (BlockPos executor: terminals) {
            if (world.getBlockEntity(executor) instanceof NetworkBlockEntityProvider blockEntity) {
                boolean execute = linkMeUp(terminals,world,executor);
                executed = execute || executed;
                blockEntity.onLinkStateChanged();
            }
        }
        return executed;
    }
    public static boolean selfLinkAll(HashSet<BlockPos> terminals, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof NetworkBlockEntityProvider blockEntity){
            blockEntity.linkUpTerminals(terminals);
            blockEntity.onLinkStateChanged();
            return true;
        }
        else return false;
    }
    public static boolean linkMeUp(HashSet<BlockPos> terminals, World world, BlockPos pos){
        boolean executed = false;
        for (BlockPos executor: terminals) {
            if (world.getBlockEntity(executor) instanceof NetworkBlockEntityProvider blockEntity) {
                 boolean execute = blockEntity.linkUpTerminal(pos);
                 executed = execute || executed;
                 blockEntity.onLinkStateChanged();
            }
        }
        return executed;
    }
    public static boolean cutMeOff(HashSet<BlockPos> terminals, World world, BlockPos pos){
        boolean executed = false;
        for (BlockPos executor: terminals) {
            if (world.getBlockEntity(executor) instanceof NetworkBlockEntityProvider blockEntity) {
                boolean execute = blockEntity.cutOffTerminal(pos);
                executed = execute || executed;
                blockEntity.onLinkStateChanged();
            }
        }
        return executed;
    }
}
