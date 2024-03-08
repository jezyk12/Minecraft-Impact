package name.synchro.blockModels;

import name.synchro.Synchro;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SynchroModelLoadingPlugin implements ModelLoadingPlugin {
    public static final SlopeModel OAK_PLANKS_SLOPE_MODEL = new SlopeModel(new Identifier(Identifier.DEFAULT_NAMESPACE, "block/oak_planks"));
    public static final ModelIdentifier SLOPE_MODEL_BLOCK =
            new ModelIdentifier(Synchro.MOD_ID, "block/slope_model", "");
    public static final ModelIdentifier OAK_PLANKS_SLOPE_MODEL_ITEM =
            new ModelIdentifier(Synchro.MOD_ID, "wooden_slope", "inventory");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            if(context.id().equals(SLOPE_MODEL_BLOCK) || context.id().equals(OAK_PLANKS_SLOPE_MODEL_ITEM)) {
                return OAK_PLANKS_SLOPE_MODEL;
            } else {
                return original;
            }
        });
    }
}
