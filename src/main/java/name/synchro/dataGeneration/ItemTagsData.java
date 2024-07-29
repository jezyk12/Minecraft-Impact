package name.synchro.dataGeneration;

import name.synchro.registrations.ModItems;
import name.synchro.registrations.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ItemTagsData extends FabricTagProvider.ItemTagProvider {
    public ItemTagsData(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.RAW_METAL_ORE).add(ModItems.LUMP_ORES, ModItems.CRACKED_ORES, ModItems.CRUSHED_ORES);
    }
}
