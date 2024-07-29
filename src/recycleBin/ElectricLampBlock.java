package name.synchro.blocks;

import name.synchro.blockEntities.ElectricLampBlockEntity;
import name.synchro.electricNetwork.AbstractConsumerBlockEntity;
import name.synchro.electricNetwork.ElectricTerminalBlockProvider;
import name.synchro.networkLink.networkBlockAPI.AbstractNetworkBlock;
import name.synchro.registrations.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class ElectricLampBlock extends AbstractNetworkBlock implements ElectricTerminalBlockProvider {
    public ElectricLampBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.LIT, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.LIT);
        super.appendProperties(builder);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(Properties.LIT,false);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricLampBlockEntity(pos,state);
    }
    @Override
    public HashSet<Direction> getLinkableDirections(BlockState state) {
        return new HashSet<>(Arrays.stream(Direction.values()).toList());
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
        if (type != ModBlockEntities.ELECTRIC_LAMP_BLOCK_ENTITY) return null;
        return (w,p,s,blockEntity) -> AbstractConsumerBlockEntity.tick(blockEntity);
    }

//    @Override
//    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (player.getInventory().getMainHandStack().getItem().equals(ModItems.UNIVERSAL_METER)){
//            if (!world.isClient) {
//                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
//                if (screenHandlerFactory != null) {
//                    player.openHandledScreen(screenHandlerFactory);
//                }
//            }
//            return ActionResult.SUCCESS;
//        }
//        return super.onUse(state, world, pos, player, hand, hit);
//    }
}

