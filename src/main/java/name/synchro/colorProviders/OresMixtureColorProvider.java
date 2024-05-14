package name.synchro.colorProviders;

import name.synchro.mixinHelper.MetalsProvider;
import name.synchro.util.Metals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class OresMixtureColorProvider implements ItemColorProvider {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return -1;
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null) return -1;
        Metals metals = ((MetalsProvider) client.world).getMetals();
        Map<Integer, Integer> contents = Metals.getMetalContentFromNbt(stackNbt);
        ArrayList<Metals.Metal> metalSort = sortContentMetal(contents, metals, 3);
        if (tintIndex < metalSort.size()) {
            return metalSort.get(tintIndex).color();
        }
        return -1;
    }

    private static ArrayList<Metals.Metal> sortContentMetal(Map<Integer, Integer> contents, Metals metals, int count) {
        if (contents.isEmpty()) return new ArrayList<>();
        List<Integer> numIds = new ArrayList<>(contents.keySet());
        numIds.sort((a, b) -> contents.get(b) - contents.get(a));
        ArrayList<Metals.Metal> metalSort = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            metalSort.add(metals.getVariants().get(numIds.get(i)));
        }
        return metalSort;
    }

}
