package name.synchro.entityRender;

import name.synchro.Synchro;
import name.synchro.entities.DuckEntity;
import name.synchro.registries.EntityRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DuckEntityRenderer extends MobEntityRenderer<DuckEntity, DuckEntityModel> {
    public DuckEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DuckEntityModel(context.getPart(EntityRenderers.DUCK_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(DuckEntity entity) {
        return Identifier.of(Synchro.MOD_ID, "textures/entity/duck.png");
    }
}
