package name.synchro.dataGeneration;

import name.synchro.registrations.RegisterBlocks;
import name.synchro.registrations.RegisterItems;
import name.synchro.blocks.SlopeBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BlockLootTablesData extends FabricBlockLootTableProvider {
    public BlockLootTablesData(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        addDrop(RegisterBlocks.BANANA_BLOCK);
        addDrop(RegisterBlocks.BANANA_LEAVES, bananaLeavesDrop());
        addDrop(RegisterBlocks.BANANA_STEM);
        addDrop(RegisterBlocks.FERTILE_DIRT);
        addDrop(RegisterBlocks.FERTILE_FARMLAND, RegisterBlocks.FERTILE_DIRT.asItem());
        for (SlopeBlock slopeBlock : SlopeBlock.SLOPE_BLOCKS) {
            addDrop(slopeBlock);
        }
    }

    @NotNull
    private Function<Block, LootTable.Builder> bananaLeavesDrop() {
        return block -> dropsWithSilkTouchOrShears(block,
                addSurvivesExplosionCondition(block, ItemEntry.builder(Items.STICK)).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.8f, 1.0f)))
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS).with(applyExplosionDecay(Items.STICK,
                                ItemEntry.builder(RegisterItems.BANANA).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f))))
                                .conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, LEAVES_STICK_DROP_CHANCE))));
    }

}
