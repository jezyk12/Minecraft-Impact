package name.synchro.blockEntities;

import name.synchro.registrations.RegisterBlockEntities;
import name.synchro.util.Liquid;
import name.synchro.util.Liquids;
import name.synchro.util.NbtTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ComplexLiquidBlockEntity extends BlockEntity {
    public final Storage storage = new Storage();
    public final Distribution distribution = new Distribution(world);

    public ComplexLiquidBlockEntity( BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.COMPLEX_LIQUID_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    public static class Storage {
        private int temperature;
        private final Map<Liquid, Integer> contents = new HashMap<>();

        public int getTemperature() {
            return temperature;
        }

        public int getVolume() {
            return this.contents.values().stream().reduce(0, Integer::sum);
        }

        public int getVolumeOf(Liquid liquid) {
            return this.contents.getOrDefault(liquid, 0);
        }

        public void fill(Liquid liquid, int droplets){
            if (droplets > 0){
                contents.put(liquid, contents.getOrDefault(liquid, 0) + droplets);
            }
        }

        public int drain(Liquid liquid, int droplets){
            if (droplets > 0){
                if (contents.containsKey(liquid)){
                    int amount = contents.get(liquid);
                    if (amount > droplets){
                        contents.put(liquid, amount - droplets);
                        return 0;
                    } else {
                        contents.remove(liquid);
                        return droplets - amount;
                    }
                }
            }
            return droplets;
        }

        public int drain(Liquid liquid){
            int value = contents.getOrDefault(liquid, 0);
            contents.remove(liquid);
            return value;
        }

        public int getColor(){
            AtomicReference<Integer> total = new AtomicReference<>(0);
            AtomicReference<Integer> alpha = new AtomicReference<>(0);
            AtomicReference<Integer> red = new AtomicReference<>(0);
            AtomicReference<Integer> green = new AtomicReference<>(0);
            AtomicReference<Integer> blue = new AtomicReference<>(0);
            this.contents.forEach((liquid, amount) -> {
                total.updateAndGet(v -> v + amount);
                int argb = liquid.color();
                alpha.updateAndGet(v -> v + (ColorHelper.Argb.getAlpha(argb) * amount));
                red.updateAndGet(v -> v + (ColorHelper.Argb.getRed(argb) * amount));
                green.updateAndGet(v -> v + (ColorHelper.Argb.getGreen(argb) * amount));
                blue.updateAndGet(v -> v + (ColorHelper.Argb.getBlue(argb) * amount));
            });
            return ColorHelper.Argb.getArgb(alpha.get() / total.get(),
                    red.get() / total.get(),
                    green.get() / total.get(),
                    blue.get() / total.get());
        }

        public float getSurfaceTension() {
            AtomicReference<Integer> total = new AtomicReference<>(0);
            AtomicReference<Float> average = new AtomicReference<>(0f);
            this.contents.forEach((liquid, amount) -> {
                total.updateAndGet(v -> v + amount);
                average.updateAndGet(v -> v + liquid.surfaceTension() * amount);
            });
            return average.get() / total.get();
        }

        public void fromNbt(NbtCompound nbt){
            if (nbt.contains(NbtTags.LIQUID_TEMPERATURE)){
                this.temperature = nbt.getInt(NbtTags.LIQUID_TEMPERATURE);
            }
            this.contents.clear();
            NbtCompound contentsNbt = nbt.getCompound(NbtTags.METALS_CONTENT);
            contentsNbt.getKeys().forEach(key -> {
                this.contents.put(Liquids.of(key), contentsNbt.getInt(key));
            });
        }

        public void writeToNbt(NbtCompound nbt){
            nbt.putInt(NbtTags.LIQUID_TEMPERATURE, this.temperature);
            NbtCompound contentsNbt = new NbtCompound();
            this.contents.forEach((liquid, amount) -> contentsNbt.putInt(liquid.name(), amount));
            nbt.put(NbtTags.LIQUID_CONTENT, contentsNbt);
        }

    }

    public static class Distribution{
        private final BlockView world;
        private final Map<BlockPos, Float> region = new HashMap<>();

        public Distribution(BlockView world) {
            this.world = world;
        }

        public void clearUnoccupied(){
            this.region.entrySet().removeIf(entry -> entry.getValue() < 1e-6f);
        }
    }
}
