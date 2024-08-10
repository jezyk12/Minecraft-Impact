package name.synchro.dataGeneration;

import name.synchro.blocks.SlopeBlock;
import name.synchro.modUtilData.DataUtil;
import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModItems;
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
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BlockLootTablesData extends FabricBlockLootTableProvider {
    private static final float[] LEAVES_STICK_DROP_CHANCE = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
    public BlockLootTablesData(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.BANANA_BLOCK);
        addDrop(ModBlocks.BANANA_LEAVES, bananaLeavesDrop());
        addDrop(ModBlocks.BANANA_STEM);
        addDrop(ModBlocks.FERTILE_DIRT);
        addDrop(ModBlocks.FERTILE_FARMLAND, ModBlocks.FERTILE_DIRT.asItem());
        for (SlopeBlock slopeBlock : SlopeBlock.SLOPE_BLOCKS) {
            addDrop(slopeBlock);
        }
        addDrop(ModBlocks.BURNT_CHARCOAL_BLOCK, drops(Items.CHARCOAL, ConstantLootNumberProvider.create(8.0f)));
        addDrop(ModBlocks.BURNT_CHARCOAL_STAIRS, drops(Items.CHARCOAL, ConstantLootNumberProvider.create(6.0f)));
        addDrop(ModBlocks.BURNT_CHARCOAL_SLAB, drops(Items.CHARCOAL, ConstantLootNumberProvider.create(4.0f)));
        addDrop(ModBlocks.BURNT_CHARCOAL_FENCE, drops(Items.CHARCOAL, ConstantLootNumberProvider.create(3.0f)));
        addDrop(ModBlocks.BURNT_CHARCOAL_PRESSURE_PLATE, drops(Items.CHARCOAL, ConstantLootNumberProvider.create(1.0f)));
    }

    @NotNull
    private Function<Block, LootTable.Builder> bananaLeavesDrop() {
        return block -> dropsWithSilkTouchOrShears(block,
                addSurvivesExplosionCondition(block, ItemEntry.builder(Items.STICK)).conditionally(TableBonusLootCondition.builder(DataUtil.entryOf(Enchantments.FORTUNE, registryLookup), 0.8f, 1.0f)))
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(createWithoutShearsOrSilkTouchCondition()).with(applyExplosionDecay(Items.STICK,
                                ItemEntry.builder(ModItems.BANANA).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f))))
                                .conditionally(TableBonusLootCondition.builder(DataUtil.entryOf(Enchantments.FORTUNE, registryLookup), LEAVES_STICK_DROP_CHANCE))));
    }

}
