package name.synchro.blocks;

import name.synchro.blockEntities.ElectricSourceBlockEntity;
import name.synchro.electricNetwork.AbstractSourceBlockEntity;
import name.synchro.electricNetwork.ElectricTerminalBlockProvider;
import name.synchro.networkLink.networkBlockAPI.AbstractNetworkBlock;
import name.synchro.registrations.RegisterBlockEntities;
import name.synchro.registrations.ItemsRegistered;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class ElectricSourceBlock extends AbstractNetworkBlock implements ElectricTerminalBlockProvider {
    public ElectricSourceBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.FACING, Direction.NORTH));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
        super.appendProperties(builder);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(Properties.FACING,ctx.getPlayerLookDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricSourceBlockEntity(pos,state);
    }
    @Override
    public HashSet<Direction> getLinkableDirections(BlockState state) {
        HashSet<Direction> linkableDirections = new HashSet<>(Arrays.stream(Direction.values()).toList());
        linkableDirections.remove(state.get(Properties.FACING));
        return linkableDirections;
    }
    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)((Object)blockEntity) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != RegisterBlockEntities.ELECTRIC_SOURCE_BLOCK_ENTITY) return null;
        return (w,p,s,blockEntity) -> AbstractSourceBlockEntity.tick(blockEntity);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getInventory().getMainHandStack().getItem().equals(ItemsRegistered.UNIVERSAL_METER)){
            if (!world.isClient) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
