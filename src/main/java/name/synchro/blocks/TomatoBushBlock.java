package name.synchro.blocks;

import name.synchro.registrations.ItemsRegistered;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class TomatoBushBlock extends PlantBlock implements Fertilizable {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    public TomatoBushBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AGE,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ItemsRegistered.TOMATO);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.25,0,0.25,0.75,0.75,0.75);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int age = state.get(AGE);
        if (random.nextInt(2) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            if (age < 3) {
                BlockState blockState = state.with(AGE, age + 1);
                world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
            else if(random.nextInt(3)==0 && (world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) || world.getBlockState(pos.down()).getBlock() instanceof AbstractFarmlandBlock)){
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsRegistered.TOMATO));
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(AGE)==3){
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsRegistered.TOMATO));
            world.setBlockState(pos,state.with(AGE,2));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE)<MAX_AGE;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i), Block.NOTIFY_LISTENERS);
    }
}
