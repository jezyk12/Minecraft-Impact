package name.synchro.synchroBlocks;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.employment.Employer;
import name.synchro.registrations.RegisterBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
            if (hit.getSide().equals(Direction.UP)) {
                ItemStack handStack = player.getStackInHand(hand);
                if (handStack.isOf(Items.WHEAT)) {
                    if (world instanceof ServerWorld serverWorld){
                        player.getStackInHand(hand).decrement(1);
                        Employer.employSuitableMob(serverWorld, millstoneBlockEntity, 4.0);
                    }
                    return ActionResult.SUCCESS;
                }
                else if (handStack.isEmpty()){
                    if (!world.isClient){
                        ItemStack ingredientStack = millstoneBlockEntity.getStack(MillstoneBlockEntity.SLOT_INPUT).copy();
                        if (!ingredientStack.isEmpty()){
                            ItemScatterer.spawn(world, pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, ingredientStack.copy());
                            millstoneBlockEntity.setStack(MillstoneBlockEntity.SLOT_INPUT, ItemStack.EMPTY);
                            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
                else if (MillstoneBlockEntity.MILLSTONE_RECIPES.containsKey(handStack.getItem())) {
                    if (!world.isClient){
                        int count = player.getStackInHand(hand).getCount();
                        ItemStack stack = player.getStackInHand(hand).split(count);
                        millstoneBlockEntity.setStack(MillstoneBlockEntity.SLOT_INPUT, stack);
                        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    }
                    return ActionResult.SUCCESS;
                }
                else return ActionResult.FAIL;
            }
            else if (Direction.Type.HORIZONTAL.test(hit.getSide())) {
                if (!world.isClient){
                    ItemStack stack = millstoneBlockEntity.removeStack(MillstoneBlockEntity.SLOT_OUTPUT);
                    Vec3d dropPos = hit.getPos().add(player.getPos().subtract(hit.getPos()).normalize().multiply(0.5));
                    ItemScatterer.spawn(world, dropPos.getX(), dropPos.getY(), dropPos.getZ(), stack);
                }
                return ActionResult.SUCCESS;
            }
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
            int speed = world.getReceivedRedstonePower(pos);
            millstoneBlockEntity.getRotationProvider().updateSpeedMultiplier(world.getTime(), speed);
            world.updateListeners(pos, this.getDefaultState(), this.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == RegisterBlockEntities.MILLSTONE_BLOCK_ENTITY){
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
                Employer.discardEmployer(serverWorld, millstoneBlockEntity);
            }
            world.removeBlockEntity(pos);
        }
    }
}
