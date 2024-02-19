package name.synchro.electricNetwork;

/**
 * 电气方块主要分为电源、用电器和蓄电池；
 * <p>电源（Source）包含1个内置电容和1个电压源，输出电压可通过GUI调节；</p>
 * 电压源有最大输出电流（produceElectronPerTick），电容电荷量抽象为Electron的数量作为数据保存；
 * <p>用电器（Consumer）包含1个内置电容和1个电阻，工作电压应为内置电容的两端电压，电流为电压除以电阻</p>
 * <p>电力能量传递逻辑：</p>
 * <p>正常运行时InternalCapacitance的Voltage值等于RatedVoltage，
 * 每tick每个Consumer根据RatedVoltage值计算这一tick的负载电流（电荷量Charge的数量），
 * 从InternalCapacitance之中减去相应的数量，并计算变化后的Voltage值，以模拟负载消耗的电量。
 * 然后遍历相连的终端（linkedTerminals）之中为Source的方块，若这个Source的内置电容的Voltage比this的更高，
 * 则认定其提供电流，最终获得一个包含所有能够提供电流的Source的表，按电压差的比例从各个电源获取Charge数量。
 * 各个电源从自身的内置电容中扣除相应数量的Charge，即使扣到0也不再产生更多影响（也就是允许凭空产生少数Charge）.
 * 更新电源的数据，并完成1 tick的电力能量传递
 * </p>
 * <p>各数据单位及参考量：（'r' stands for "...OfRedstone"）</p>
 * <p>红石电量 Q = 500 [Cr]</p>
 * <p>红石电流 I = 500 [Cr/tick] Display-> 10.0m [Ar = MCr/s]</p>
 * <p>红石电阻 R = 200.0 [Ωr]</p>
 * <p>红石电压 U = 100.0k [Cr*Ωr/tick] Display-> 2.0 [Vr = MCr*Ωr/s]</p>
 * <p>红石电容 C = 5.0m [tick/Cr] Display-> 500p [Fr = s/MCr]</p>
 * <p>红石功率 P = 50M [Ωr*Cr^2/tick^2] Display-> 20.0m [Wr = Ωr*MCr^2/s^2]</p>
 */
public interface ElectricBlockEntityProvider {
    enum WorkingSituation {
        NORMAL,
        OVERLOADED,
        UNDERLOADED,
    }
    String CAPACITANCE = "capacitance";
    String VOLTAGE = "charge_voltage";
    float EPSILON = 0.00001f;
    float getPortVoltage();
    float getRatedVoltage();
    void setRatedVoltage(float value);
    void replaceCapacityWith(ElectricCapacity capacity);
    WorkingSituation getWorkingSituation();
    void updateDisplayData(float voltage, float current);
    float getVoltage();
    float getCurrent();
    enum EnergyFunctionType {
        CAN_OUTPUT,
        CAN_INPUT,
        BOTH_SIDES
    }
    EnergyFunctionType getEnergyFunctionType();
}
