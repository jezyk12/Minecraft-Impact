package name.synchro.dataGeneration;

import name.synchro.registrations.ModBlocks;
import name.synchro.registrations.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class TranslationENUSData extends FabricLanguageProvider {
    public TranslationENUSData(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        ModBlocks.ALL.forEach(block -> addSimple(translationBuilder, block));
        ModItems.ALL.forEach(item -> addSimple(translationBuilder, item));
        addTranslationFile(translationBuilder);
    }

    protected void addSimple(TranslationBuilder builder, Block block){
        builder.add(block, getName(Registries.BLOCK.getId(block).getPath()));
    }

    protected void addSimple(TranslationBuilder builder, Item item){
        builder.add(item, getName(Registries.ITEM.getId(item).getPath()));
    }

    private static String getName(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] arr = path.split("_");
        for (String string : arr) {
            stringBuilder.append(string.substring(0, 1).toUpperCase()).append(string.substring(1)).append(" ");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    private void addTranslationFile(TranslationBuilder builder) {
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/synchro/lang/additional.json").get();
            builder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
