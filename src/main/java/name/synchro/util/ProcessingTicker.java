package name.synchro.util;

public interface ProcessingTicker {
    int getProcessingTicks();
    void setProcessingTicks(int ticks);
    void tick();
}
