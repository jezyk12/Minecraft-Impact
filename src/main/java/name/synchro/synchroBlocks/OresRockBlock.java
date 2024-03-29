package name.synchro.synchroBlocks;

import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class OresRockBlock extends ExperienceDroppingBlock {
    private static final IntProvider DEFAULT_EXP = UniformIntProvider.create(1,6);
    private static final String[] ROCK_NAMES =  new String[]{"Sekite", "Aoite", "Midorite", "Muraxkite", "Gamite", "Nganite", "Haakite", "Baakite"};
    public OresRockBlock(Settings settings) {
        super(settings, DEFAULT_EXP);
    }
}
