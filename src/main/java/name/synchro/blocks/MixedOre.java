package name.synchro.blocks;

import name.synchro.items.RawMixedOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;

import java.util.List;

import static name.synchro.registrations.ModItems.setNbt;

@Deprecated
public class MixedOre extends Block {
    public MixedOre(Settings settings) {
        super(settings);
    }
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> dropItems = super.getDroppedStacks(state,builder);
        for(ItemStack itemStack : dropItems){
            if(itemStack.getItem() instanceof RawMixedOre){
                NbtCompound oreNbt = new NbtCompound();
                for(String ore: RawMixedOre.AVAILABLE_ORE_LIST){
                    int oreContent = Random.create().nextBetween(0,128);
                    oreNbt.putInt(ore, oreContent);
                }
                setNbt(itemStack, oreNbt);
            }
        }
        return dropItems;
    }

}
