package name.synchro.util;

import java.util.HashMap;
import java.util.Map;

public final class Liquids {
    private static final Map<Object, Liquid> ENTRIES = new HashMap<>();

    public static final Liquid WATER = builder().named( "water").color(0x7f7f7fcf).tension(1 / 16f).melting(273).boiling(373).density(1000).build();
    public static final Liquid LAVA = builder().named("lava").color(0x7f7f0000).tension(1 / 8f).melting(1500).boiling(4000).density(3100).build();
    public static final Liquid MILK = builder().named("milk").color(0x7fffffff).tension(1 / 16f).melting(270).boiling(373).density(1030).build();
    public static final Liquid HONEY = builder().named("honey").color(0x7f7f3f00).tension(1 / 8f).melting(280).boiling(373).density(1400).build();

    public static Builder builder(){
        return new Builder();
    }

    public static Liquid of(String name){
        if (ENTRIES.containsKey(name)){
            return ENTRIES.get(name);
        }
        return null;
    }

    public static class Builder{
        private int color = 0x7f7f7f7f;
        private float tension = 1 / 16f;
        private int meltingTemperature = 0;
        private int boilingTemperature = Integer.MAX_VALUE;
        private int density = 1000;
        private String name = "empty";

        public Builder color(int color){
            this.color = color;
            return this;
        }

        public Builder tension(float tension){
            this.tension = tension;
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

        public Builder named(String name){
            this.name = name;
            return this;
        }

        public Liquid build(){
            Liquid instance = new Liquid(name, color, tension, meltingTemperature, boilingTemperature, density);
            ENTRIES.put(name, instance);
            return instance;
        }
    }
}
