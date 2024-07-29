package name.synchro.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.PlantBlock;

public class BushWithFlowers extends PlantBlock {
    public BushWithFlowers(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return createCodec(BushWithFlowers::new);
    }
}
