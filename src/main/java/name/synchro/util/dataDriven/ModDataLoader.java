package name.synchro.util.dataDriven;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import name.synchro.Synchro;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public final class ModDataLoader {
    private static final ModDataLoader INSTANCE = new ModDataLoader();
    private static final Gson GSON = new Gson();
    public static final String DICTIONARY = "mod_config";
    private Map<Identifier, JsonElement> contents;
    private ModDataLoader(){}

    public static ModDataLoader getInstance() {
        return INSTANCE;
    }

    public void reload(ResourceManager manager){
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        ResourceFinder resourceFinder = ResourceFinder.json(DICTIONARY);
        for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(manager).entrySet()) {
            Identifier key = entry.getKey();
            Identifier resourceId = resourceFinder.toResourceId(key);
            try {
                try (Reader reader = entry.getValue().getReader()) {
                    JsonElement jsonData = JsonHelper.deserialize(GSON, reader, JsonElement.class);
                    JsonElement duplicated = map.put(resourceId, jsonData);
                    if (duplicated != null) {
                        Synchro.LOGGER.warn("Duplicate data file ignored with ID {}", resourceId);
                    }
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                Synchro.LOGGER.error("Couldn't parse data file {} from {}", resourceId, key, exception);
            }
        }
        this.contents = map;
    }

    public Map<Identifier, JsonElement> getContents(){
        return this.contents;
    }
}
