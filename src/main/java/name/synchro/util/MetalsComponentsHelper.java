package name.synchro.util;

import com.google.common.collect.ImmutableMap;
import name.synchro.Synchro;
import name.synchro.mixinHelper.MetalsProvider;
import name.synchro.registrations.BlocksRegistered;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.*;

public final class MetalsComponentsHelper {
    /**
     * Compatible with {@link FluidConstants}
     * <p>Unit: Droplet(drp)</p>
     */
    public static final int DRP_BLOCK = 81000;
    public static final int DRP_INGOT = 9000;
    public static final int DRP_NUGGET = 1000;
    public static final int DRP_LUMP_ORES = 72000;  // 8 Ingots
    public static final int DRP_CRACKED_ORES = 54000;   // 6 Ingots
    public static final int DRP_CRUSHED_ORES = 36000;   // 4 Ingots
    public static final int DRP_ORES_DUST = 27000;  // 3 Ingots
    public static final Metals.Attributes NO_ATTRIBUTES_BIAS = new Metals.Attributes(0, 0, 0, 0, 0, 0);
    public static final Metals.Attributes ALL_FIELD_NOT_BAD = new Metals.Attributes(1, 1, 1, 1, 1, 1);
    public static final BiomesFunction DEFAULT_BIOMES_FUNCTION = new BiomesFunction(18000, 8, 2, 1, NO_ATTRIBUTES_BIAS);
    public static final ImmutableMap<RegistryKey<Biome>, BiomesFunction> FUNCTIONS_VANILLA = ImmutableMap.<RegistryKey<Biome>, BiomesFunction>builder()
            .put(BiomeKeys.PLAINS, DEFAULT_BIOMES_FUNCTION)
            .put(BiomeKeys.DRIPSTONE_CAVES, new BiomesFunction(27000, 12, 3, 1, NO_ATTRIBUTES_BIAS))
            .put(BiomeKeys.LUSH_CAVES, new BiomesFunction(22500, 10, 3, 1, NO_ATTRIBUTES_BIAS))
            .put(BiomeKeys.DEEP_DARK, new BiomesFunction(30000, 6, 1, 0, ALL_FIELD_NOT_BAD))
            .put(BiomeKeys.DESERT, new BiomesFunction(22500, 8, 4, 2, NO_ATTRIBUTES_BIAS))
            .put(BiomeKeys.DARK_FOREST, new BiomesFunction(20000, 6, 1, 0, new Metals.Attributes(0,0,0,0,0,1)))
            .put(BiomeKeys.BADLANDS, new BiomesFunction(22500, 6, 1, 0, new Metals.Attributes(0,0,0,0,1,0)))
            .put(BiomeKeys.DEEP_OCEAN, new BiomesFunction(27000, 6, 1, 0, new Metals.Attributes(0,0,1,0,0,0)))
            .put(BiomeKeys.SWAMP, new BiomesFunction(22500, 6, 1, 0, new Metals.Attributes(0,0,0,1,0,0)))
            .put(BiomeKeys.DEEP_COLD_OCEAN, new BiomesFunction(27000, 6, 1, 0, new Metals.Attributes(1,0,0,0,0,0)))
            .put(BiomeKeys.SAVANNA, new BiomesFunction(20000, 6, 1, 0, new Metals.Attributes(0,1,0,0,0,0)))
            .build();
    public static final ImmutableMap<Block, Metals.Attributes> STATE_MULTIPLIERS = ImmutableMap.<Block, Metals.Attributes>builder()
            .put(BlocksRegistered.ROCK_CRACKED, new Metals.Attributes(0, 0, 1, 1, 2, 2))
            .put(BlocksRegistered.ROCK_COARSE, new Metals.Attributes(2, 1, 1, 0, 1, 0))
            .put(BlocksRegistered.ROCK_DARK, new Metals.Attributes(0, 2, 1, 1, 1, 1))
            .put(BlocksRegistered.ROCK_LIGHT, new Metals.Attributes(1, 2, 2, 1, 0, 1))
            .put(BlocksRegistered.ROCK_SCRATCH, new Metals.Attributes(1, 1, 0, 2, 1, 0))
            .put(BlocksRegistered.ROCK_SMOOTH, new Metals.Attributes(2, 0, 1, 2, 2, 0))
            .put(BlocksRegistered.ROCK_SHINY, new Metals.Attributes(1, 0, 2, 1, 0, 2))
            .put(BlocksRegistered.ROCK_STRATIFORM, new Metals.Attributes(0, 0, 1, 0, 2, 1))
            .build();


    public record BiomesFunction(int abundance, int maxVariety, int majorsCount, int mediumCount, Metals.Attributes attributes){}

    public static List<Integer> createComponentsList(World world, BlockPos pos){
        BiomesFunction biomesFunction = FUNCTIONS_VANILLA.getOrDefault(world.getBiomeAccess().getBiome(pos).getKey().orElse(BiomeKeys.THE_VOID), DEFAULT_BIOMES_FUNCTION);
        Random posRandom = new Random(pos.asLong() ^ ((MetalsProvider) world).getMetals().seed);
        if (biomesFunction == null) {
            Synchro.LOGGER.error("Fail to create components list: Null Parameters");
            return List.of();
        }
        int maxVariety = biomesFunction.maxVariety();
        int majorsCount = biomesFunction.majorsCount();
        int mediumCount = biomesFunction.mediumCount();
        if (maxVariety - (maxVariety >> 2) - majorsCount - mediumCount < 0 || mediumCount < 0 || majorsCount < 1 || maxVariety < 1){
            Synchro.LOGGER.error("Fail to create components list: Illegal Parameters");
            return List.of();
        }
        int variety = maxVariety - posRandom.nextInt(maxVariety >> 2);
        int minorsCount = variety - majorsCount - mediumCount;
        int minorRadio = 1;
        int majorRadio = minorsCount * 24;
        int mediumRadioSum = 0;
        int[] mediumRadios = new int[0];
        if (mediumCount > 0){
            mediumRadios = new int[mediumCount];
            for (int i = 0; i < mediumCount; i++) {
                mediumRadios[i] = (mediumCount - i) * majorRadio / (mediumCount + 1);
                mediumRadioSum += mediumRadios[i];
            }
        }
        int radioSum = minorRadio * minorsCount + majorRadio * majorsCount + mediumRadioSum;
        List<Integer> components = new ArrayList<>();
        int abundance = biomesFunction.abundance();
        for (int i = 0; i < majorsCount; i++) {
            int units = majorRadio * abundance / radioSum;
            components.add(units - posRandom.nextInt(units >> 4));
        }
        components.sort((a, b) -> b - a);
        if (mediumCount > 0){
            for (int i = 0; i < mediumCount; i++) {
                int units = mediumRadios[i] * abundance / radioSum;
                components.add(units - posRandom.nextInt(units >> 4));
            }
        }
        for (int i = 0; i < minorsCount; i++) {
            int units = minorRadio * abundance / radioSum;
            components.add(units + posRandom.nextInt(Math.max((units >> 4), 4)));
        }
        return components;
    }

    public static List<Metals.Metal> createMetalList(World world, BlockState state, BlockPos pos){
        Metals metals = ((MetalsProvider) world).getMetals();
        RegistryKey<Biome> biomeKey = world.getBiomeAccess().getBiome(pos).getKey().orElse(BiomeKeys.THE_VOID);
        List<Metals.Metal> metalsList = new ArrayList<>(metals.getVariants());
        metalsList.sort((a, b) -> {
            double aPoint = getMetalPoint(a, metals.seed, pos, biomeKey, state.getBlock());
            double bPoint = getMetalPoint(b, metals.seed, pos, biomeKey, state.getBlock());
            return Double.compare(bPoint, aPoint);
        });
        return metalsList;
    }

    public static int getRockContent(Map<Integer, Integer> contents, int drpTotal) {
        int drpContent = 0;
        for (int content : contents.values()) {
            drpContent += content;
        }
        return Math.max(drpTotal - drpContent, 0);
    }

    private static double getMetalPoint(Metals.Metal metal, long seed, BlockPos pos, RegistryKey<Biome> biomeKey, Block block){
        double raw = getRawMetalPoint(metal, seed, pos);
        double biomeMultiplier = 1 + MathFunctions.getAngleCos(metal.attributes().toVector(), FUNCTIONS_VANILLA.getOrDefault(biomeKey, DEFAULT_BIOMES_FUNCTION).attributes().toVector());
        double stateMultiplier = 1 + MathFunctions.getAngleCos(metal.attributes().toVector(), STATE_MULTIPLIERS.getOrDefault(block, NO_ATTRIBUTES_BIAS).toVector());
        return raw * biomeMultiplier * stateMultiplier;
    }

    private static double getRawMetalPoint(Metals.Metal metal, long seed, BlockPos pos){
        double[][][] points = new double[2][2][2];
        int x0 = (pos.getX() >> 6) << 6;
        int y0 = (pos.getY() >> 6) << 6;
        int z0 = (pos.getY() >> 6) << 6;
        for (int dx = 0; dx < 2; dx++) for (int dy = 0; dy < 2; dy++) for (int dz = 0; dz < 2; dz++){
            long rawId = (seed + metal.numId()) ^ BlockPos.asLong(x0 + dx, y0 + dy, z0 + dz);
            points[dx][dy][dz] = MathFunctions.randomlyMapDouble(rawId);
        }
        Vec3i delta = pos.subtract(new Vec3i(x0, y0, z0));
        double dx = delta.getX() / 64.;
        double dy = delta.getY() / 64.;
        double dz = delta.getZ() / 64.;
        return MathHelper.lerp3(dx, dy, dz, points[0][0][0], points[1][0][0], points[0][1][0], points[1][1][0], points[0][0][1], points[1][0][1], points[0][1][1], points[1][1][1]);
    }

    public static Map<Integer, Integer> mapMetals(World world, BlockState state, BlockPos pos){
        List<Integer> componentsList = createComponentsList(world, pos);
        List<Metals.Metal> metalsList = createMetalList(world, state, pos);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < componentsList.size(); i++) {
            Metals.Metal metal = metalsList.get(i);
            int content = componentsList.get(i);
            map.put(metal.numId(), content);
        }
        return map;
    }
}
