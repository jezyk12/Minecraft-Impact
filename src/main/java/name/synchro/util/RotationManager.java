package name.synchro.util;

import net.minecraft.nbt.NbtCompound;

public interface RotationManager {
    String SPEED_MULTIPLIER = "speed";
    String REFERENCE_ROTATION = "reference";
    String LAST_SPEED_CHANGE_TIME = "last_change";

    RotationProvider getRotationProvider();

    /**
     * Stores the rotation data. Both server and client can use these data and methods to get the current rotation.
     * <p> These data should only be updated when rotation speed is changed. </p>
     */
    class RotationProvider {
        private int speedMultiplier;
        private int referenceRotation;
        private long lastSpeedChangeTime;

        public RotationProvider(int speedMultiplier, int referenceRotation, long lastSpeedChangeTime) {
            this.speedMultiplier = speedMultiplier;
            this.referenceRotation = referenceRotation;
            this.lastSpeedChangeTime = lastSpeedChangeTime;
        }

        public RotationProvider(long initialTime, int initialRotation){
            this(0, (int) (initialRotation + initialTime) % 360, initialTime);
        }

        public int getSpeedMultiplier() {
            return speedMultiplier;
        }

        protected void setSpeedMultiplier(int speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
        }

        public int getReferenceRotation() {
            return referenceRotation;
        }

        protected void setReferenceRotation(int referenceRotation) {
            this.referenceRotation = referenceRotation;
        }

        public long getLastSpeedChangeTime() {
            return lastSpeedChangeTime;
        }

        protected void setLastSpeedChangeTime(long lastSpeedChangeTime) {
            this.lastSpeedChangeTime = lastSpeedChangeTime;
        }

        /**
         * Use this method to change the rotation speed and setup all rotation data.
         * <p> This should only be called when trying to change the rotation speed.</p>
         * @param timeNow Commonly be world.getTime()
         */
        public void updateSpeedMultiplier(long timeNow, int newSpeedMultiplier) {
            int speedMultiplier = getSpeedMultiplier();
            long lastSpeedChangeTime = getLastSpeedChangeTime();
            long lastRotation = (timeNow - lastSpeedChangeTime) * speedMultiplier;
            setReferenceRotation((int) ((getReferenceRotation() + lastRotation) % 360));
            setSpeedMultiplier(newSpeedMultiplier);
            setLastSpeedChangeTime(timeNow);
        }

        public NbtCompound toNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.putInt(SPEED_MULTIPLIER, getSpeedMultiplier());
            nbt.putInt(REFERENCE_ROTATION, getReferenceRotation());
            nbt.putLong(LAST_SPEED_CHANGE_TIME, getLastSpeedChangeTime());
            return nbt;
        }
    }


    default void updateSpeedMultiplier(long timeNow, int newSpeedMultiplier) {
        this.getRotationProvider().updateSpeedMultiplier(timeNow, newSpeedMultiplier);
    }

    default float getRecentRotation(long timeNow, float tickDelta) {
        RotationProvider provider = this.getRotationProvider();
        int speedMultiplier = provider.getSpeedMultiplier();
        long lastSpeedChangeTime = provider.getLastSpeedChangeTime();
        long lastRotation = (timeNow - lastSpeedChangeTime) * speedMultiplier;
        return (provider.getReferenceRotation() + lastRotation) % 360 + tickDelta * speedMultiplier;
    }

    default int getRecentRotation(long timeNow) {
        return (int) getRecentRotation(timeNow, 0);
    }

    default NbtCompound createRotationNbt() {
        return this.getRotationProvider().toNbt();
    }

    default void setupRotationFromNbt(NbtCompound nbt) {
        RotationProvider provider = this.getRotationProvider();
        provider.setSpeedMultiplier(nbt.getInt(SPEED_MULTIPLIER));
        provider.setReferenceRotation(nbt.getInt(REFERENCE_ROTATION));
        provider.setLastSpeedChangeTime(nbt.getLong(LAST_SPEED_CHANGE_TIME));
    }
}
