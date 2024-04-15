package name.synchro.electricNetwork;

public class ElectricCapacity {
    public final float CAPACITANCE;
    private int charge = 0;

    public ElectricCapacity(float capacitance) {
        CAPACITANCE = capacitance;
    }

    public ElectricCapacity(float capacitance, float voltage) {
        CAPACITANCE = capacitance;
        charge = (int) (capacitance * voltage);
    }

    public float getVoltage() {
        return charge / CAPACITANCE;
    }

    public void increaseCharge(int value){
        charge += value;
    }

    public int decreaseCharge(int value){
        int current = 0;
        if (charge - value >= 0){
            charge -= value ;
            current = value;
        }
        else {
            current = charge;
            charge = 0;
        }
        return current;
    }
}
