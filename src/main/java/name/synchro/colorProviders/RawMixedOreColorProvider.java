package name.synchro.colorProviders;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static name.synchro.items.RawMixedOre.*;

public class RawMixedOreColorProvider implements ItemColorProvider {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        HashMap<Integer,Integer> colorWeightMap = new HashMap<>();
        if(stack.hasNbt()){
            assert stack.getNbt() != null;
            int stoneContent = TOTAL_MASS;
            for(String ore: AVAILABLE_ORE_LIST){
                colorWeightMap.put(oreColorMap.get(ore), stack.getNbt().getInt(ore));
                stoneContent-=stack.getNbt().getInt(ore);
            }
            //colorWeightMap.put(oreColorMap.get("stone"), stoneContent);
            return blendColors(colorWeightMap);
        }
       return -1;
    }
    public int blendColors(HashMap<Integer, Integer> colorWeights) {
        int numColors = colorWeights.size();  // 颜色数量
        int[] colors = new int[numColors];  // 颜色数组
        int[] weights = new int[numColors];  // 比重数组

        int index = 0;
        for (Map.Entry<Integer, Integer> entry : colorWeights.entrySet()) {
            colors[index] = entry.getKey();
            weights[index] = entry.getValue();
            index++;
        }

        // 检查颜色和比重数量是否一致
        if (colors.length != weights.length) {
            throw new IllegalArgumentException("Number of colors and weights should be the same.");
        }

        // 归一化比重
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        double[] normalizedWeights = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            normalizedWeights[i] = (double) weights[i] / totalWeight;
        }

        // 初始化混合后的颜色
        int[] blendedColor = new int[3];

        // 按比重混合颜色
        for (int i = 0; i < numColors; i++) {
            int color = colors[i];
            double weight = normalizedWeights[i];
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            blendedColor[0] += (int) (r * weight);
            blendedColor[1] += (int) (g * weight);
            blendedColor[2] += (int) (b * weight);
        }

        // 合并RGB通道的值
        int r = Math.min(blendedColor[0], 255);
        int g = Math.min(blendedColor[1], 255);
        int b = Math.min(blendedColor[2], 255);

        return (r << 16) | (g << 8) | b;
    }
}
