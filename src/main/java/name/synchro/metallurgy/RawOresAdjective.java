package name.synchro.metallurgy;

import net.minecraft.util.Rarity;
import net.minecraft.util.StringIdentifiable;

@Deprecated
public enum RawOresAdjective implements StringIdentifiable {
    COARSE("coarse", Rarity.COMMON),
    SHINY("shiny", Rarity.COMMON),
    FRAGILE("fragile", Rarity.COMMON),
    HARD("hard", Rarity.COMMON),
    COMPLEX("complex", Rarity.COMMON),
    PURE("pure", Rarity.COMMON),
    GLITTERY("glittery", Rarity.UNCOMMON),
    HEAVY("heavy", Rarity.UNCOMMON),
    ENERGETIC("energetic", Rarity.RARE),
    UNSTABLE("unstable", Rarity.RARE),
    PERFECT("perfect", Rarity.EPIC);


    public final String name;
    public final Rarity rarity;

    RawOresAdjective(String name, Rarity rarity){
        this.name = name;
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getTranslationKey(RawOresAdjective adjective){
        return "ores.synchro.adjective." + adjective.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public enum Type {
        BLOCK(new RawOresAdjective[]{COARSE, SHINY, FRAGILE, HARD, COMPLEX, PURE, GLITTERY}),
        BLOCK_TEXTURE(new RawOresAdjective[]{COARSE, SHINY}),
        BLOCK_HARDNESS(new RawOresAdjective[]{FRAGILE, HARD}),
        BLOCK_PURITY(new RawOresAdjective[]{COMPLEX, PURE}),
        ITEM(new RawOresAdjective[]{FRAGILE, HARD, COMPLEX, PURE, GLITTERY, HEAVY, ENERGETIC, UNSTABLE, PERFECT});

        public final RawOresAdjective[] adjectives;

        Type(RawOresAdjective[] adjectives) {
            this.adjectives = adjectives;
        }
    }
}
