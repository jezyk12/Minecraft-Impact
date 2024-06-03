package name.synchro.util;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class Liquids {
    private static final Map<Object, Liquid> ENTRIES = new HashMap<>();

    public static final Liquid WATER = builder().color(0x7f7f7fcf).melting(273).boiling(373).density(1000).from(Blocks.WATER).build();
    public static final Liquid LAVA = builder().color(0x7f7f0000).melting(1500).boiling(4000).density(3100).from(Blocks.LAVA).build();
    public static final Liquid MILK = builder().color(0x7fffffff).melting(270).boiling(373).density(1030).from(Items.MILK_BUCKET).build();
    public static final Liquid HONEY = builder().color(0x7f7f3f00).melting(280).boiling(373).density(1400).from(Items.HONEY_BOTTLE, Blocks.HONEY_BLOCK).build();

    public static Builder builder(){
        return new Builder();
    }

    public static Liquid of(Object obj){
        if (ENTRIES.containsKey(obj)){
            return ENTRIES.get(obj);
        }
        return null;
    }

    public static class Builder{
        private int color = 0x7f7f7f7f;
        private int meltingTemperature = 0;
        private int boilingTemperature = Integer.MAX_VALUE;
        private int density = 1000;
        @Nullable
        private Object[] source;

        public Builder color(int color){
            this.color = color;
            return this;
        }

        public Builder melting(int meltingTemperature){
            this.meltingTemperature = meltingTemperature;
            return this;
        }

        public Builder boiling(int boilingTemperature){
            this.boilingTemperature = boilingTemperature;
            return this;
        }

        public Builder density(int density){
            this.density = density;
            return this;
        }

        public Builder from(Object... source){
            this.source = source;
            return this;
        }

        public Liquid build(){
            Liquid instance = new Liquid(color, meltingTemperature, boilingTemperature, density);
            for (Object obj : source){
                ENTRIES.put(obj, instance);
            }
            return instance;
        }
    }
}
