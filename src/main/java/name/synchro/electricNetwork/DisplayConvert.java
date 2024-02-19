package name.synchro.electricNetwork;

public class DisplayConvert {
    /**<p>各数据单位及参考量：（'r' stands for "...OfRedstone"）</p>
     * <p>红石电量 Q = 500 [Cr]</p>
     * <p>红石电流 I = 500 [Cr/tick] Display-> 10.0m [Ar = MCr/s]</p>
     * <p>红石电阻 R = 200.0 [Ωr]</p>
     * <p>红石电压 U = 100.0k [Cr*Ωr/tick] Display-> 2.0 [Vr = MCr*Ωr/s]</p>
     * <p>红石电容 C = 5.0m [tick/Cr] Display-> 500p [Fr = s/MCr]</p>
     * <p>红石功率 P = 50M [Ωr*Cr^2/tick^2] Display-> 20.0m [Wr = Ωr*MCr^2/s^2]</p>
     */
    public enum PhysicalQuantities{
        CHARGE,
        RESISTANCE,
        CURRENT,
        VOLTAGE,
        CAPACITANCE,
        POWER,
        UNKNOWN
    }
    public static String toDisplayStr(PhysicalQuantities type,float value){
       String unit;
       float displayValue;
       switch (type){
           case CURRENT -> {
               displayValue = value*20/1e6f;
               unit = "Ar";
           }
           case VOLTAGE -> {
               displayValue = value*20/1e6f;
               unit = "Vr";
           }
           case CHARGE -> {
               displayValue = value;
               unit = "Cr";
           }
           case POWER -> {
               displayValue = value*400/1e12f;
               unit = "Wr";
           }
           case CAPACITANCE -> {
               displayValue = value/20*1e6f;
               unit = "Fr";
           }
           case RESISTANCE -> {
               displayValue = value;
               unit = "Ωr";
           }
           default -> {
               displayValue = value;
               unit = "?r";
           }
       }
       return formatFloat(displayValue) + unit;
    }
    private static final String[] SUFFIXES = {"f","p","n","μ","m"," ","k","M","G","T","P","E"};
    private static String formatFloat(float value){
        int i = 5;
        while ((value < 1f) && (i > 0)){
            value *= 1000f;
            i -= 1;
        }
        while ((value >= 1000f) && (i < (SUFFIXES.length - 1))){
            value /= 1000f;
            i += 1;
        }
        String numStr;
        if (value >= 1000f){
            numStr = "999.9";
        }
        else if(value >= 100f){
            numStr = String.format("%.1f",value);
        }
        else if (value >= 10f){
            numStr = String.format("%.2f",value);
        }
        else if (value >= 1f){
            numStr = String.format("%.3f",value);
        }
        else numStr = "0.000";
        return numStr + SUFFIXES[i];
    }
}
