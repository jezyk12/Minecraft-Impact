package name.synchro.modUtilData.simpleData;

public record MetalsConfig(int variants) implements SimpleData<MetalsConfig>{
    @Override
    public Type<MetalsConfig> getType() {
        return null;
    }

    @Override
    public MetalsConfig get() {
        return null;
    }
}
