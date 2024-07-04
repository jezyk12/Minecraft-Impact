package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.entities.DuckEntityModel;
import name.synchro.entities.DuckEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class EntityRenderers {
    public static final EntityModelLayer DUCK_LAYER =
            registerMobRender(ModEntities.DUCK, "duck", "duck", DuckEntityModel::getTexturedModelData, DuckEntityRenderer::new);

    private static <T extends MobEntity> EntityModelLayer registerMobRender(EntityType<T> entityType, String path, String name, EntityModelLayerRegistry.TexturedModelDataProvider provider, EntityRendererFactory<T> factory){
        EntityModelLayer self = new EntityModelLayer(new Identifier(Synchro.MOD_ID, path) , name);
        EntityModelLayerRegistry.registerModelLayer(self, provider);
        EntityRendererRegistry.register(entityType, factory);
        return self;
    }

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered all entity renderings for " + Synchro.MOD_ID);
    }
}
