package name.synchro.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.PlantBlock;

public class BushBlock extends PlantBlock {
    public BushBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return createCodec(BushBlock::new);
    }
}
