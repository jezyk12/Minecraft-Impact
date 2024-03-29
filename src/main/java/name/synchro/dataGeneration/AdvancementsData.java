package name.synchro.dataGeneration;

import name.synchro.Synchro;
import name.synchro.registrations.RegisterBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class AdvancementsData extends FabricAdvancementProvider {
    public AdvancementsData(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.create()
                .display(RegisterBlocks.FERTILE_DIRT,
                        Text.translatable("advancements.farming.fertile_dirt.title"),
                        Text.translatable("advancements.farming.fertile_dirt.description"),
                        new Identifier(Synchro.MOD_ID, "textures/block/fertile_dirt.png"),
                        AdvancementFrame.TASK,
                        true, true, false)
                .criterion("got_fertile_dirt", InventoryChangedCriterion.Conditions.items(RegisterBlocks.FERTILE_DIRT))
                .build(consumer, Synchro.MOD_ID + "/root");
    }
}
