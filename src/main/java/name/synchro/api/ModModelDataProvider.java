package name.synchro.api;

import name.synchro.Synchro;
import name.synchro.blocks.SlopeBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public abstract class ModModelDataProvider extends FabricModelProvider {
    public static final Identifier SLOPE_STRAIGHT_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_straight");
    public static final Identifier SLOPE_INNER_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_inner");
    public static final Identifier SLOPE_OUTER_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_outer");
    public static final Model SLOPE_STRAIGHT = new Model(Optional.of(SLOPE_STRAIGHT_ID), Optional.of("_straight"), TextureKey.ALL);
    public static final Model SLOPE_INNER = new Model(Optional.of(SLOPE_INNER_ID), Optional.of("_inner"), TextureKey.ALL);
    public static final Model SLOPE_OUTER = new Model(Optional.of(SLOPE_OUTER_ID), Optional.of("_outer"), TextureKey.ALL);

    public ModModelDataProvider(FabricDataOutput output) {
        super(output);
    }

	@Override
	public abstract void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator);

	@Override
	public abstract void generateItemModels(ItemModelGenerator itemModelGenerator);

    protected static void registerSlope(BlockStateModelGenerator generator, SlopeBlock block) {
		TextureMap textures;
		if (block.getTexture() == null){
			textures = TextureMap.all(block.getBaseBlockState().getBlock());
		}
		else textures = TextureMap.all(block.getTexture());
        Identifier s = SLOPE_STRAIGHT.upload(ModModelDataProvider.createPathId(block, "slopes/", "_straight"), textures, generator.modelCollector);
        Identifier i = SLOPE_INNER.upload(ModModelDataProvider.createPathId(block, "slopes/", "_inner"), textures, generator.modelCollector);
        Identifier o = SLOPE_OUTER.upload(ModModelDataProvider.createPathId(block, "slopes/", "_outer"), textures, generator.modelCollector);
        generator.blockStateCollector.accept(ModModelDataProvider.createSlopeBlockState(block, i, s, o));
        generator.registerParentedItemModel(block, s);
    }

    protected static Identifier createPathId(Block block, String prefix, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath((path) -> "block/" + prefix + path + suffix);
    }

    private static BlockStateSupplier createSlopeBlockState(SlopeBlock slopeBlock, Identifier innerId, Identifier straightId, Identifier outerId) {
        return VariantsBlockStateSupplier.create(slopeBlock)
                .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE)
                        .register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId))
                        .register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId))
                        .register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId))
                        .register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId))
                        .register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId))
                        .register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, straightId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, outerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.UVLOCK, true))
                        .register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, innerId)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.UVLOCK, true)));
    }
}
