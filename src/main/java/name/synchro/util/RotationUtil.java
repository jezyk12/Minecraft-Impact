package name.synchro.util;

@Deprecated
public class RotationUtil {
    public static final int MAX_ROUND_TICKS_MULTIPLIER = 10;
    public static final int MAX_ROUND_TICKS = 360 * MAX_ROUND_TICKS_MULTIPLIER;

    public static int generateRelativeRotations(long globalTime, int absoluteRotation) {
        int globalRotation = (int) (globalTime % MAX_ROUND_TICKS);
        return (globalRotation - absoluteRotation) % MAX_ROUND_TICKS;
    }

    public static float getNowRotation(long globalTime, int absoluteRotation, int relativeRotation, boolean isRotating, float tickDelta) {
        int globalRotation = (int) (globalTime % MAX_ROUND_TICKS);
        if (isRotating) {
            return ((globalRotation - relativeRotation) % MAX_ROUND_TICKS +  tickDelta) / (float) MAX_ROUND_TICKS_MULTIPLIER;
        } else {
            return (float) absoluteRotation / MAX_ROUND_TICKS_MULTIPLIER;
        }
    }
}
