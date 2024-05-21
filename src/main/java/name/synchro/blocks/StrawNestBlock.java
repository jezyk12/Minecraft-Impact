package name.synchro.blocks;

import name.synchro.blockEntities.StrawNestBlockEntity;
import name.synchro.employment.Employer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StrawNestBlock extends Block implements BlockEntityProvider {
    static final VoxelShape COLLISION_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.25, 1);
    static final VoxelShape OUTLINE_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
    public StrawNestBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StrawNestBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld && world.getBlockEntity(pos) instanceof StrawNestBlockEntity strawNestBlockEntity) {
            if (!strawNestBlockEntity.getStack(0).isEmpty()) {
                dropStack(serverWorld, pos, strawNestBlockEntity.getStack(0).copy());
                strawNestBlockEntity.setStack(0, ItemStack.EMPTY);
            }
            else {
                if (player.getStackInHand(hand).isOf(Items.WHEAT_SEEDS)){
                    if (Employer.employSuitableMob(serverWorld, strawNestBlockEntity, 3.0))
                        player.getStackInHand(hand).decrement(1);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            if (world instanceof ServerWorld serverWorld && world.getBlockEntity(pos) instanceof StrawNestBlockEntity strawNestBlockEntity) {
                dropStack(world, pos, strawNestBlockEntity.getStack(0));
                Employer.discardEmployer(serverWorld, strawNestBlockEntity);
            }
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof StrawNestBlockEntity strawNestBlockEntity){
             return Math.min((strawNestBlockEntity.getStack(0).getCount() + 3) / 4, 15);
         }
         else return 0;
    }
}
