package name.synchro.electricNetwork;

import name.synchro.networkLink.networkBlockEntityAPI.AbstractNetworkBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public abstract class AbstractConsumerBlockEntity extends AbstractNetworkBlockEntity implements ElectricBlockEntityProvider {
    private ElectricCapacity capacity;
    private float ratedVoltage;
    private float resistance;
    private HashSet<ElectricBlockEntityProvider> sources;
    private int tickCurrent = 0;
    public final ElectricDataDisplay DATA;
    @Override
    public float getRatedVoltage() {
        return ratedVoltage;
    }
    @Override
    public float getPortVoltage(){
        return capacity.getVoltage();
    }
    @Override
    public void setRatedVoltage(float value) {
        ratedVoltage = value;
    }
    public void setResistance(float value){
        resistance = value;
    }

    @Override
    public float getVoltage() {
        return DATA.voltage;
    }

    @Override
    public float getCurrent() {
        return DATA.current;
    }

    @Override
    public WorkingSituation getWorkingSituation() {
         float voltagePercentage = (capacity.getVoltage()/getRatedVoltage());
         if (voltagePercentage > 1.1f) return WorkingSituation.OVERLOADED;
         else if (voltagePercentage < 0.9f) return WorkingSituation.UNDERLOADED;
         else return WorkingSituation.NORMAL;
    }

    @Override
    public EnergyFunctionType getEnergyFunctionType() {
        return EnergyFunctionType.CAN_INPUT;
    }

    private Pair<Float,HashSet<ElectricBlockEntityProvider>> getPortState(){
        float maxU = capacity.getVoltage();
        HashSet<ElectricBlockEntityProvider> normalSources = new HashSet<>();
        for (ElectricBlockEntityProvider source: sources){
            float Us = source.getPortVoltage();
            float deltaU = Us - maxU;
            if (Math.abs(deltaU) < EPSILON){
                normalSources.add(source);
            }
            else if (deltaU > EPSILON) {
                maxU = Us;
                normalSources.clear();
                normalSources.add(source);
            }
        }
        return new Pair<>(maxU,normalSources);
    }

    @Override
    public void onLinkStateChanged() {
        acknowledgeSources();
    }

    private void tickUpdate(){
        if (sources==null){
            sources=new HashSet<>();
            acknowledgeSources();
        }
        Pair<Float,HashSet<ElectricBlockEntityProvider>> portState = getPortState();
        boolean toRevise = portState.getRight().isEmpty();
        resistanceConsumesEnergy(portState);
        if (toRevise) portState.setLeft(capacity.getVoltage());
        chargeCapacityFromSources(portState);
        updateDisplayData(capacity.getVoltage(),tickCurrent);
    }
    private void chargeCapacityFromSources(Pair<Float, HashSet<ElectricBlockEntityProvider>> portState) {
        float portVoltage = portState.getLeft();
        if (portVoltage - capacity.getVoltage() > EPSILON){
            int current = (int) (capacity.CAPACITANCE * (portVoltage - capacity.getVoltage()));
            int receivedCurrent = receiveChargeFromSources(current /* *70/100 这里乘一个小于1的数可以模拟电压损耗*/, portState);
            capacity.increaseCharge(receivedCurrent);
        }
    }
    private void resistanceConsumesEnergy(Pair<Float, HashSet<ElectricBlockEntityProvider>> portState) {
        float u = capacity.getVoltage();
        float r = resistance;
        int i = (int) (u/r);
        tickCurrent = capacity.decreaseCharge(i);
    }
    private int receiveChargeFromSources(int current, Pair<Float,HashSet<ElectricBlockEntityProvider>> portState) {
        int receivedCurrent = 0;
        for (ElectricBlockEntityProvider blockEntity: portState.getRight()){
            if (blockEntity instanceof AbstractSourceBlockEntity sourceBlockEntity) {
                receivedCurrent += sourceBlockEntity.offerChargeToConsumer(current / portState.getRight().size());
            }
        }
        return receivedCurrent;
    }

    public void updateDisplayData(float voltage, float current){
        DATA.voltage = voltage;
        DATA.current = current;
        DATA.power = voltage * current;
    }

    public static <T extends BlockEntity> void tick(T blockEntity) {
        if (blockEntity instanceof AbstractConsumerBlockEntity consumerBlockEntity) {
            consumerBlockEntity.tickUpdate();
        }
    }

    public void acknowledgeSources(){
        if (world!=null){
            if (sources == null)sources = new HashSet<>();
            sources.clear();
            for (BlockPos pos : getLinkState()) {
                if (world.getBlockEntity(pos) instanceof ElectricBlockEntityProvider blockEntity) {
                    if (blockEntity.getEnergyFunctionType() == EnergyFunctionType.CAN_OUTPUT) {
                        sources.add(blockEntity);
                    }
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);
        replaceCapacityWith(new ElectricCapacity(nbt.getFloat(CAPACITANCE), nbt.getFloat(VOLTAGE)));
    }

    @Override
    protected void writeNbt(NbtCompound toWriteNbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        toWriteNbt.putFloat(CAPACITANCE,capacity.CAPACITANCE);
        toWriteNbt.putFloat(VOLTAGE,capacity.getVoltage());
        super.writeNbt(toWriteNbt, wrapperLookup);
    }

    @Override
    public void replaceCapacityWith(ElectricCapacity capacity) {
        this.capacity = capacity;
    }

    public AbstractConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ElectricCapacity capacity, float ratedVoltage, float internalResistance) {
        super(type, pos, state);
        this.capacity = capacity;
        this.ratedVoltage = ratedVoltage;
        this.resistance = internalResistance;
        this.DATA = new ElectricDataDisplay();
        this.sources = null;
    }
}
