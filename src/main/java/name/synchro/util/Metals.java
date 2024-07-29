package name.synchro.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public final class Metals {
    private final World world;
    final long seed;
    private final ImmutableList<Metal> variants;
    public static final Metal EMPTY_METAL = new Metal(-1, -1, new Attributes(0, 0, 0, 0, 0, 0));

    public record Metal(int numId, int color, Attributes attributes){
        private static Metal from(Random random, int numId, int variantsCount, float shift){
            final float h = ((numId + shift + 0.3f + 0.4f * random.nextFloat()) / variantsCount) % 1.0f;
            final float s = 0.1f + 0.1f * random.nextFloat();
            final float b = 0.8f + 0.1f * random.nextFloat();
            return new Metal(numId, Color.HSBtoRGB(h, s, b), Attributes.from(random));
        }
    }

    public record Attributes(double hardness, double strength, double density, double meltingPoint, double conductivity, double reducibility){
        private static Attributes from(Random random){
            return new Attributes(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        }

        public double[] toVector(){
            return new double[]{hardness(), strength(), density(), meltingPoint(), conductivity(), reducibility()};
        }
    }

    public Metals(World world, long seed, int variantsCount) {
        this.world = world;
        this.seed = seed;
        Random random = new Random(seed);
        List<Metal> variantsMutable = new ArrayList<>();
        float shift = random.nextInt(variantsCount) + random.nextFloat();
        for (int i = 0; i < variantsCount; i++) {
            variantsMutable.add(i, Metal.from(random, i, variantsCount, shift));
        }
        this.variants = ImmutableList.copyOf(variantsMutable);
    }

    public List<Metal> getVariants() {
        return variants;
    }

    public Map<Integer, Integer> getContents(BlockState state, BlockPos pos){
        return MetalsComponentsHelper.mapMetals(this.world, state, pos);
    }

    public static Map<Integer, Integer> getMetalContentFromNbt(NbtCompound nbt){
        if (nbt.contains(NbtTags.METALS_CONTENT)) {
            NbtCompound nbtMap = nbt.getCompound(NbtTags.METALS_CONTENT);
            Map<Integer, Integer> map = new HashMap<>();
            for (String key : nbtMap.getKeys()) {
                map.put(Integer.parseInt(key), nbtMap.getInt(key));
            }
            return map;
        }
        return Map.of();
    }

    public static void writeMetalContentToNbt(NbtCompound nbt, Map<Integer, Integer> content){
        NbtCompound nbtMap = new NbtCompound();
        for (int i : content.keySet()) {
            nbtMap.putInt(String.valueOf(i), content.get(i));
        }
        nbt.put(NbtTags.METALS_CONTENT, nbtMap);
    }

    public static void combineMetalsNbt(NbtCompound shouldBeOverwrite, int count1, NbtCompound shouldKeepUnchanged, int count2){
        Map<Integer, Integer> map1 = getMetalContentFromNbt(shouldBeOverwrite);
        Map<Integer, Integer> map2 = getMetalContentFromNbt(shouldKeepUnchanged);
        Map<Integer, Integer> mapResult = new HashMap<>();
        int divide = count1 + count2;
        Set<Integer> keySet = new HashSet<>(map1.keySet());
        keySet.addAll(map2.keySet());
        for (int numId : keySet) {
            mapResult.put(numId, (map1.getOrDefault(numId, 0) * count1 + map2.getOrDefault(numId, 0) * count2) / divide);
        }
        shouldBeOverwrite.remove(NbtTags.METALS_CONTENT);
        writeMetalContentToNbt(shouldBeOverwrite, mapResult);
    }

    public interface Provider {
        Metals getMetals();
    }

//    /**
//     * Server-side only. Modify the proportion of the minor components in mixed ore.
//     * <p> Ranges:</p>
//     * <p>temperature: -2.22 ~ +2.22</p>
//     * <p>vegetation: -1.69 ~ +1.69</p>
//     * <p>continents: -3.66 ~ +3.66</p>
//     * <p>erosion: -2.42 ~ +2.42</p>
//     * <p>depth: -2.00 ~ +2.00</p>
//     * <p>ridges: -2.86 ~ +2.86</p>
//     *
//     * @return List of ratio of the metals contained in the block at the given position.
//     */
//    @Deprecated
//    public List<Float> modifyMetalContentFromPos(ServerWorld world, BlockPos pos, BlockState state, Predicate<RegistryKey<World>> worldPredicate) {
//        NoiseRouter noiseRouter = null;
//        RegistryKey<World> registryKey = world.getRegistryKey();
//        if (worldPredicate.test(registryKey)) {
//            noiseRouter = world.getChunkManager().getNoiseConfig().getNoiseRouter();
//        }
//        if (noiseRouter != null) {
//            DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(pos.getX(), pos.getY(), pos.getZ());
//            float t = normalize(noiseRouter.temperature().sample(unblendedNoisePos), -2.23, 2.23);
//            float v = normalize(noiseRouter.vegetation().sample(unblendedNoisePos), -1.70, 1.70);
//            float c = normalize(noiseRouter.continents().sample(unblendedNoisePos), -3.67, 3.67);
//            float e = normalize(noiseRouter.erosion().sample(unblendedNoisePos), -2.43, 2.43);
//            float d = normalize(noiseRouter.depth().sample(unblendedNoisePos), -2.01, 2.01);
//            float r = normalize(noiseRouter.ridges().sample(unblendedNoisePos), -2.87, 2.87);
//            List<Float> list = new ArrayList<>(getVariants().size());
//            for (int i = 0; i < getVariants().size(); i++) {
//                Metal metal = getVariants().get(i);
//                float fitness = getFitness(new float[]{t, v, c, e, d, r}, metal);
//                list.set(i, fitness);
//            }
//            float abundance = 0.1f + d * (0.1f + 0.05f * random.nextFloat());
//            return standardize(list, abundance);
//        }
//        return List.of();
//    }
//
//    @Deprecated
//    private static float normalize(double value, double min, double max) {
//        return (float) ((value - min) / (max - min));
//    }
//    @Deprecated
//    private static List<Float> standardize(List<Float> list, float multiplier) {
//        float sum = 0;
//        for (Float f : list) {
//            sum += f;
//        }
//        if (sum == 0) {
//            return list;
//        }
//        for (int i = 0; i < list.size(); i++) {
//            list.set(i, list.get(i) / sum * multiplier);
//        }
//        return list;
//    }
//    @Deprecated
//    private static float getFitness(float[] noiseValues, Metal metal) {
//        float lNoise = (float) Math.sqrt(noiseValues[0] * noiseValues[0] + noiseValues[1] * noiseValues[1] + noiseValues[2] * noiseValues[2] + noiseValues[3] * noiseValues[3] + noiseValues[4] * noiseValues[4] + noiseValues[5] * noiseValues[5]);
//        float lMetal = (float) Math.sqrt(metal.meltingPoint * metal.meltingPoint + metal.reducibility * metal.reducibility + metal.conductivity * metal.conductivity + metal.hardness * metal.hardness + metal.density * metal.density + metal.strength * metal.strength);
//        return (noiseValues[0] * metal.meltingPoint + noiseValues[1] * metal.reducibility + noiseValues[2] * metal.conductivity
//                + noiseValues[3] * metal.hardness + noiseValues[4] * metal.density + noiseValues[5] * metal.strength) / (lNoise * lMetal);
//    }


}
