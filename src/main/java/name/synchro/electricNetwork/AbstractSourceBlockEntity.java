package name.synchro.electricNetwork;

import name.synchro.networkLink.networkBlockEntityAPI.AbstractNetworkBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractSourceBlockEntity extends AbstractNetworkBlockEntity implements ElectricBlockEntityProvider{
    private ElectricCapacity capacity;
    private float ratedVoltage;
    private int maxCurrent;
    private int tickCurrentRemains;
    public final ElectricDataDisplay DATA;
    @Override
    public float getRatedVoltage() {
        return ratedVoltage;
    }

    @Override
    public void setRatedVoltage(float value) {
        ratedVoltage = value;
    }

    public float getPortVoltage(){
        return capacity.getVoltage();
    }

    @Override
    public float getVoltage() {
        return DATA.voltage;
    }

    @Override
    public float getCurrent() {
        return DATA.current;
    }

    public float getTickCurrent(){
        return maxCurrent - tickCurrentRemains;
    }

    public int offerChargeToConsumer(int current){
        int decreasedCharge = capacity.decreaseCharge(current);
        chargeCapacity();
        return decreasedCharge;
    }

    private int produceCharge(int current){
        if ((tickCurrentRemains - current) >=0){
            tickCurrentRemains -= current;
            return current;
        }
        else {
            int actuallyProduce = tickCurrentRemains;
            tickCurrentRemains = 0;
            return actuallyProduce;
        }
    }

    @Override
    public void onLinkStateChanged() {}

    @Override
    public void updateDisplayData(float voltage, float current){
        DATA.voltage = voltage;
        DATA.current = current;
        DATA.power = voltage * current;
    }

    public void tickUpdate(){
        int current = maxCurrent - tickCurrentRemains;
        tickCurrentRemains = maxCurrent;
        chargeCapacity();
        updateDisplayData(capacity.getVoltage(), current);

    }

    private void chargeCapacity() {
        float deltaU = ratedVoltage - capacity.getVoltage();
        if (deltaU > EPSILON){
            int current = (int) (capacity.CAPACITANCE * deltaU);
            capacity.increaseCharge(produceCharge(current));
        }
    }

    @Override
    public ElectricBlockEntityProvider.WorkingSituation getWorkingSituation() {
        float voltagePercentage = (capacity.getVoltage()/getRatedVoltage());
        if (voltagePercentage > 1.05f) return ElectricBlockEntityProvider.WorkingSituation.OVERLOADED;
        else if (voltagePercentage < 0.95f) return ElectricBlockEntityProvider.WorkingSituation.UNDERLOADED;
        else return ElectricBlockEntityProvider.WorkingSituation.NORMAL;
    }
    public static <T extends BlockEntity> void tick(T blockEntity) {
        if (blockEntity instanceof AbstractSourceBlockEntity sourceBlockEntity) {
            sourceBlockEntity.tickUpdate();
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

    @Override
    public EnergyFunctionType getEnergyFunctionType() {
        return EnergyFunctionType.CAN_OUTPUT;
    }
    public AbstractSourceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ElectricCapacity capacity, float ratedVoltage, int maxCurrent) {
        super(type, pos, state);
        this.capacity = capacity;
        this.maxCurrent = maxCurrent;
        this.ratedVoltage = ratedVoltage;
        DATA = new ElectricDataDisplay();
    }
}
