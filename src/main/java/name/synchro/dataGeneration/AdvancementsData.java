package name.synchro.dataGeneration;

import name.synchro.Synchro;
import name.synchro.registrations.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsData extends FabricAdvancementProvider {

    public AdvancementsData(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        Advancement root = Advancement.Builder.create()
                .display(ModBlocks.FERTILE_DIRT,
                        Text.translatable("advancements.farming.fertile_dirt.title"),
                        Text.translatable("advancements.farming.fertile_dirt.description"),
                        Identifier.of(Synchro.MOD_ID, "textures/block/fertile_dirt.png"),
                        AdvancementFrame.TASK,
                        true, true, false)
                .criterion("got_fertile_dirt", InventoryChangedCriterion.Conditions.items(ModBlocks.FERTILE_DIRT))
                .build(consumer, Synchro.MOD_ID + "/root").value();
    }

}
