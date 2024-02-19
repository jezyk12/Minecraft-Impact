package name.synchro.synchroItems;

import name.synchro.electricNetwork.DisplayConvert;
import name.synchro.electricNetwork.ElectricBlockEntityProvider;
import name.synchro.networkLink.networkBlockEntityAPI.AbstractNetworkBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Objects;

public class DataRod extends Item {
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!context.getWorld().isClient) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof AbstractNetworkBlockEntity blockEntity){
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.literal("[Data Rod] This: "+context.getBlockPos()));
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.literal("[Data Rod] Linked: "+blockEntity.getLinkState()));
                if (blockEntity instanceof ElectricBlockEntityProvider electricBlockEntity){
                    Objects.requireNonNull(context.getPlayer()).sendMessage(
                            Text.literal("[Data Rod] Voltage: "+
                                    DisplayConvert.toDisplayStr(DisplayConvert.PhysicalQuantities.VOLTAGE,electricBlockEntity.getVoltage())));
                    Objects.requireNonNull(context.getPlayer()).sendMessage(
                            Text.literal("[Data Rod] Current: "+
                                    DisplayConvert.toDisplayStr(DisplayConvert.PhysicalQuantities.CURRENT,electricBlockEntity.getCurrent())));
                    Objects.requireNonNull(context.getPlayer()).sendMessage(
                            Text.literal("[Data Rod] Power: "+
                                    DisplayConvert.toDisplayStr(DisplayConvert.PhysicalQuantities.POWER,
                                            electricBlockEntity.getVoltage()*electricBlockEntity.getCurrent())));
                }
            }
            else{
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.literal("[Data Rod] This: " + context.getBlockPos()));
            }
            Objects.requireNonNull(context.getPlayer()).sendMessage(
                    Text.literal("-----------------------------"));
        }
        return ActionResult.SUCCESS;
    }
    public DataRod(Settings settings) {
        super(settings);
    }
}
