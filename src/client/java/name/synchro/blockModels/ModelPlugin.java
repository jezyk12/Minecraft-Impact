package name.synchro.blockModels;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import name.synchro.Synchro;
import name.synchro.api.ModModelDataProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ModelPlugin implements ModelLoadingPlugin {
    private static final String ITEM = "item";
    public static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("missingno"));
    public static final Identifier MILLSTONE_MOVABLE_ID = Identifier.of(Synchro.MOD_ID, "block/millstone/millstone_movable");
    public static final Identifier MILLSTONE_WOOD_ID = Identifier.of(Synchro.MOD_ID, "block/millstone/millstone_wood");
    protected static final Map<Identifier, Supplier<UnbakedModel>> BUILTIN_MODELS = ImmutableMap.of(
            ModModelDataProvider.SLOPE_STRAIGHT_ID, () -> new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.STRAIGHT),
            ModModelDataProvider.SLOPE_INNER_ID, () -> new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.INNER),
            ModModelDataProvider.SLOPE_OUTER_ID, () -> new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.OUTER));

    private UnbakedModel onResolve(ModelResolver.Context context){
        Identifier id = context.id();
        if (id.getNamespace().equals(Synchro.MOD_ID)) {
            return BUILTIN_MODELS.getOrDefault(id, () -> null).get();
        }
        return null;
    }

    private UnbakedModel onLoad(UnbakedModel original, ModelModifier.OnLoad.Context context) {
        Identifier id = context.resourceId();
        ModelIdentifier modelId = context.topLevelId();
        Function<Identifier, UnbakedModel> loader = context::getOrLoadModel;
        if (id != null){
            if (id.getNamespace().equals(Synchro.MOD_ID)) {
                return getModelFromOriginal(original, id, loader);
            }
        }
        else if (modelId != null){
            if (modelId.id().getNamespace().equals(Synchro.MOD_ID)) {
                return getModelFromOriginal(original, modelId.id(), loader);
            }
        }
        return original;
    }

    private UnbakedModel getModelFromOriginal(UnbakedModel original, Identifier id, Function<Identifier, UnbakedModel> loader){
        if (original instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel.parentId != null) {
            if (BUILTIN_MODELS.containsKey(jsonUnbakedModel.parentId)) {
                UnbakedModel model = BUILTIN_MODELS.get(jsonUnbakedModel.parentId).get();
                if (model instanceof UnbakedSlopeModel unbakedSlopeModel) {
                    unbakedSlopeModel.setSprite(jsonUnbakedModel.textureMap.get(TextureKey.ALL.getName()).left().orElse(DEFAULT_SPRITE_ID));
                    return unbakedSlopeModel;
                }
            } else if (isModParentedItemModel(jsonUnbakedModel, id)) {
                return loader.apply(jsonUnbakedModel.parentId);
            }
        }
        return original;
    }

    private boolean isModParentedItemModel(JsonUnbakedModel model, Identifier id){
        if (model.parentId.getNamespace().equals(Synchro.MOD_ID)) {
            if (id.getPath().split("/")[0].equals(ITEM)) {
                return model.textureMap.isEmpty();
            }
        }
        return false;
    }

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.addModels(MILLSTONE_MOVABLE_ID);
        pluginContext.addModels(MILLSTONE_WOOD_ID);
        pluginContext.resolveModel().register(this::onResolve);
        pluginContext.modifyModelOnLoad().register(this::onLoad);
    }
}
