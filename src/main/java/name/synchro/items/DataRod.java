package name.synchro.items;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.electricNetwork.DisplayConvert;
import name.synchro.electricNetwork.ElectricBlockEntityProvider;
import name.synchro.networkLink.networkBlockEntityAPI.AbstractNetworkBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Objects;

public class DataRod extends Item {
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        //electricDataGet(context);
        outputNbt(context);
        //millstoneSpeedManage(context);
        return ActionResult.SUCCESS;
    }

    private static void millstoneSpeedManage(ItemUsageContext context) {
        if (!context.getWorld().isClient){
            World world = context.getWorld();
            if (world.getBlockEntity(context.getBlockPos()) instanceof MillstoneBlockEntity blockEntity){
                blockEntity.updateSpeedMultiplier(world.getTime(), (blockEntity.getRotationProvider().getSpeedMultiplier() + 2) % 60);
                world.updateListeners(context.getBlockPos(), world.getBlockState(context.getBlockPos()), world.getBlockState(context.getBlockPos()), Block.NOTIFY_ALL);
            }
        }
    }

    private static void outputNbt(ItemUsageContext context) {
        if (!context.getWorld().isClient){
            World world = context.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(context.getBlockPos());
            if (blockEntity != null) {
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.of(new StringNbtWriter().apply(blockEntity.createNbtWithIdentifyingData(context.getWorld().getRegistryManager()))));
            }
            else{
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.of("No block entity at this block."));
            }
        }
    }

    private static void electricDataGet(ItemUsageContext context) {
        if(!context.getWorld().isClient) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof AbstractNetworkBlockEntity blockEntity){
                Objects.requireNonNull(context.getPlayer()).sendMessage(
                        Text.literal("[Data Rod] This: "+ context.getBlockPos()));
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
    }

    public DataRod(Settings settings) {
        super(settings);
    }
}
