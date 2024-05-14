package name.synchro.util;

public final class MathFunctions {
    private static final double DOUBLE_UNIT = 0x1.0p-53;

    public static int randomlyMap(int value){
        for (int i = 0; i < 4; i++) {
            value = (value << 15) + value + i;
        }
        return value;
    }

    public static int randomlyMap(long seed){
        return randomlyMap((int) (seed >>> 32) ^ (int) seed);
    }

    public static int randomlyMapBetween(int value, int min, int max){
        int r = randomlyMap(value);
        return min + ((r & Integer.MAX_VALUE) % (max - min));
    }

    public static int randomlyMapBetween(long seed, int min, int max){
        return randomlyMapBetween((int) (seed >>> 32) ^ (int) seed, min, max);
    }

    public static double randomlyMapDouble(long seed){
        int i0 = randomlyMap(seed);
        int i1 = randomlyMap(i0);
        return ((((long) i0 & 0x3ffffff) << 27) | (i1 & 0x7ffffff)) * DOUBLE_UNIT;
    }

    public static double getAngleCos(double[] vec1, double[] vec2){
        if (vec1.length != vec2.length){
            throw new IllegalArgumentException("Vectors must have the same length");
        }
        double dotProduct = 0;
        double squaredNorm1 = 0;
        double squaredNorm2 = 0;
        for (int i = 0; i < vec1.length; i++){
            dotProduct += vec1[i] * vec2[i];
            squaredNorm1 += vec1[i] * vec1[i];
            squaredNorm2 += vec2[i] * vec2[i];
        }
        if (dotProduct == 0 || squaredNorm1 == 0 || squaredNorm2 == 0){
            return 0;
        }
        return dotProduct / Math.sqrt(squaredNorm1 * squaredNorm2);
    }
}
