package name.synchro.blocks;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.employment.Employer;
import name.synchro.registrations.ModBlockEntities;
import name.synchro.registrations.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Millstone extends Block implements BlockEntityProvider {
    public Millstone(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MillstoneBlockEntity millstoneBlockEntity) {
            ItemStack handStack = player.getStackInHand(hand);
            if (handStack.isOf(ModItems.FRESH_FORAGE)) {
                if (world instanceof ServerWorld serverWorld) {
                    if (Employer.employSuitableMob(serverWorld, millstoneBlockEntity, 4.0))
                        player.getStackInHand(hand).decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MillstoneBlockEntity(pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MillstoneBlockEntity millstoneBlockEntity) {
            int redstonePower = world.getReceivedRedstonePower(pos);
            if (redstonePower > 0) millstoneBlockEntity.lock();
            else millstoneBlockEntity.unlock();
            world.updateListeners(pos, this.getDefaultState(), this.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.MILLSTONE_BLOCK_ENTITY){
            return (world1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof MillstoneBlockEntity millstoneBlockEntity) {
                    millstoneBlockEntity.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            if (world instanceof ServerWorld serverWorld && serverWorld.getBlockEntity(pos) instanceof MillstoneBlockEntity millstoneBlockEntity) {
                dropStack(world, pos, millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_INPUT));
                dropStack(world, pos, millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_OUTPUT));
                dropStack(world, pos, millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_FEED));
                Employer.discardEmployer(serverWorld, millstoneBlockEntity);
            }
            world.removeBlockEntity(pos);
        }
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof MillstoneBlockEntity millstoneBlockEntity){
            return millstoneBlockEntity;
        }
        return super.createScreenHandlerFactory(state, world, pos);
    }
}
