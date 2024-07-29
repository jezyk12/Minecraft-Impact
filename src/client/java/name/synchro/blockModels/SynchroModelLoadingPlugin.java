package name.synchro.blockModels;

import name.synchro.Synchro;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SynchroModelLoadingPlugin implements ModelLoadingPlugin {
    private static final String ITEM_ID = "inventory";
    public static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of("block/bedrock"));
    public static final Identifier SLOPE_STRAIGHT_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_straight");
    public static final Identifier SLOPE_INNER_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_inner");
    public static final Identifier SLOPE_OUTER_ID = Identifier.of(Synchro.MOD_ID, "block/slopes/slope_outer");
    public static final Identifier MILLSTONE_MOVABLE_ID = Identifier.of(Synchro.MOD_ID, "block/millstone/millstone_movable");
    public static final Identifier MILLSTONE_WOOD_ID = Identifier.of(Synchro.MOD_ID, "block/millstone/millstone_wood");
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.addModels(MILLSTONE_MOVABLE_ID);
        pluginContext.addModels(MILLSTONE_WOOD_ID);
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            Identifier id = context.resourceId();
            ModelIdentifier modelId = context.topLevelId();
            if (id != null){
                if (id.getNamespace().equals(Synchro.MOD_ID)) {
                    return getModelFromOriginal(original);
                }
            }
            else if (modelId != null){
                if (modelId.id().getNamespace().equals(Synchro.MOD_ID)) {
                    return getModelFromOriginal(original);
                }
            }
            return original;
        });
    }

    private UnbakedModel getModelFromOriginal(UnbakedModel original){
        if (original instanceof JsonUnbakedModel jm && jm.parentId != null) {
            if (jm.parentId.equals(SLOPE_STRAIGHT_ID)){
                Optional<SpriteIdentifier> spriteIdContainer = jm.textureMap.get("all").left();
                SpriteIdentifier spriteId = spriteIdContainer.orElse(DEFAULT_SPRITE_ID);
                return new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.STRAIGHT, spriteId);
            }
            else if (jm.parentId.equals(SLOPE_INNER_ID)) {
                Optional<SpriteIdentifier> spriteIdContainer = jm.textureMap.get("all").left();
                SpriteIdentifier spriteId = spriteIdContainer.orElse(DEFAULT_SPRITE_ID);
                return new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.INNER, spriteId);
            }
            else if (jm.parentId.equals(SLOPE_OUTER_ID)) {
                Optional<SpriteIdentifier> spriteIdContainer = jm.textureMap.get("all").left();
                SpriteIdentifier spriteId = spriteIdContainer.orElse(DEFAULT_SPRITE_ID);
                return new UnbakedSlopeModel(UnbakedSlopeModel.SlopeShape.OUTER, spriteId);
            }
        }
        return original;
    }

}
