package name.synchro.metallurgy;

public enum OresElements {
    COAL("coal", 1, 10, 12),
    COPPER("copper", 8, 30, 64),
    IRON("iron", 10, 50, 56),
    GOLD("gold", 80, 10, 197),
    EMERALD("emerald", 90, 90, 40, true, false, false),
    DIAMOND("diamond", 98, 140, 12, true, false, false),
    LAPIS("lapis_lazuli", 85, 80, 35, true, true, false),
    REDSTONE("redstone", 50, 10, 238, false, true, true)
    ;

    public final String name;
    /**
     * Should be 0 ~ 100
     */
    public final int appearance;
    public static final int MAX_APPEARANCE = 100;
    /**
     * Should be 0 ~ 150
     */
    public final int hardness;
    public static final int MAX_HARDNESS = 150;
    /**
     * Should be 0 ~ 300
     */
    public final int weight;
    public static final int MAX_WEIGHT = 300;
    public final boolean isGlittery;
    public final boolean isEnergetic;
    public final boolean isUnstable;


    OresElements(String name, int appearance, int hardness, int weight, boolean isGlittery, boolean isEnergetic, boolean isUnstable) {
        this.name = name;
        this.appearance = appearance;
        this.hardness = hardness;
        this.weight = weight;
        this.isGlittery = isGlittery;
        this.isEnergetic = isEnergetic;
        this.isUnstable = isUnstable;
    }

    OresElements(String name, int appearance, int hardness, int weight) {
        this(name, appearance, hardness, weight, false, false, false);
    }
}
