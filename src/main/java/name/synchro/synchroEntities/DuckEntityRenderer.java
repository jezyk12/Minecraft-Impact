package name.synchro.synchroEntities;

import name.synchro.Synchro;
import name.synchro.registrations.RegisterEntityRendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DuckEntityRenderer extends MobEntityRenderer<DuckEntity, DuckEntityModel> {
    public DuckEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DuckEntityModel(context.getPart(RegisterEntityRendering.DUCK_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(DuckEntity entity) {
        return new Identifier(Synchro.MOD_ID, "textures/entity/duck.png");
    }
}
