package name.synchro.synchroItems;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RawMixedOre extends Item {
    public static final String[] AVAILABLE_ORE_LIST =
            {"coal","copper","iron","gold","redstone"};
    public static final HashMap<String ,Integer> oreColorMap = new HashMap<>();
    private static void initOreColorMap(){
        oreColorMap.put("coal",0x393e46);
        oreColorMap.put("iron",0xfedec8);
        oreColorMap.put("copper",0xfc9982);
        oreColorMap.put("gold",0xfaea2e);
        oreColorMap.put("redstone",0xff0000);
       // oreColorMap.put("stone",0x); //stone content also decides color
    }
    public static final Short TOTAL_MASS = 1024;
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt()) {
            assert stack.getNbt() != null;
            for (String ore : AVAILABLE_ORE_LIST) {
                int oreContent = stack.getNbt().getInt(ore);
                if(oreContent!=0){
                    tooltip.add(Text.translatable("ore.synchro." + ore)
                        .append(Text.translatable("info.synchro.content"))
                        .append(": "+String.format("%.1f", (float) oreContent / TOTAL_MASS * 100)+"%"));
                }
            }
        }
    }

    public RawMixedOre(Settings settings) {
        super(settings);
        initOreColorMap();
    }
}
