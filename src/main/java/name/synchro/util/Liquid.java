package name.synchro.util;

public record Liquid(String name, int color, float surfaceTension ,int meltingTemperature, int boilingTemperature, int density) {
    @Override
    public String toString() {
        return name();
    }
}
