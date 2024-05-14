package name.synchro.util;

public interface ProcessingHandler {
    int getProcessingTime();
    void setProcessingTime(int ticks);
    void tick();
}
