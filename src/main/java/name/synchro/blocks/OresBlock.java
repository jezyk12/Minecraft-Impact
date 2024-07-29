package name.synchro.blocks;

import name.synchro.metallurgy.RawOresAdjective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.intprovider.UniformIntProvider;

@Deprecated
public class OresBlock extends ExperienceDroppingBlock {
    public static final EnumProperty<RawOresAdjective> TEXTURE = EnumProperty.of("texture", RawOresAdjective.class, RawOresAdjective.Type.BLOCK_TEXTURE.adjectives);
    public static final EnumProperty<RawOresAdjective> HARDNESS = EnumProperty.of("hardness", RawOresAdjective.class, RawOresAdjective.Type.BLOCK_HARDNESS.adjectives);
    public static final EnumProperty<RawOresAdjective> PURITY = EnumProperty.of("purity", RawOresAdjective.class, RawOresAdjective.Type.BLOCK_PURITY.adjectives);
    public static final BooleanProperty GLITTERY = BooleanProperty.of("glittery");

    public final Block baseBlock;

    public OresBlock(Block baseBlock, Settings settings) {
        super(UniformIntProvider.create(1, 6), settings);
        this.baseBlock = baseBlock;
        setDefaultState(getDefaultState().with(TEXTURE, RawOresAdjective.SHINY).with(HARDNESS, RawOresAdjective.HARD).with(PURITY, RawOresAdjective.PURE).with(GLITTERY, false));
    }

    public OresBlock(Settings settings) {
        this(Blocks.STONE, settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TEXTURE, HARDNESS, PURITY, GLITTERY);
    }
}
