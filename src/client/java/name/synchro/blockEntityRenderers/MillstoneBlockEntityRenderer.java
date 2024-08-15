package name.synchro.blockEntityRenderers;

import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.blockModels.ModelPlugin;
import name.synchro.util.BlockEntityExtraCollisionDisplay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import java.util.Objects;

public class MillstoneBlockEntityRenderer implements BlockEntityRenderer<MillstoneBlockEntity> {
    BlockModelRenderer blockModelRenderer;
    BakedModelManager bakedModelManager;
    ItemRenderer itemRenderer;
    public MillstoneBlockEntityRenderer(BlockEntityRendererFactory.Context context){
        this.blockModelRenderer = context.getRenderManager().getModelRenderer();
        this.bakedModelManager = context.getRenderManager().getModels().getModelManager();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MillstoneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BakedModel movableModel = this.bakedModelManager.getModel(ModelPlugin.MILLSTONE_MOVABLE_ID);
        BakedModel woodModel = this.bakedModelManager.getModel(ModelPlugin.MILLSTONE_WOOD_ID);
        BlockEntityExtraCollisionDisplay.displayCollisions(entity.getExtraCollisionsOrigin(tickDelta), matrices, vertexConsumers.getBuffer(RenderLayer.getLines()));
        matrices.push();
        matrices.translate(0f, 12 / 16f, 0f);
        float rotation = entity.getRecentRotation(Objects.requireNonNull(entity.getWorld()).getTime(), tickDelta);;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation), 0.5f, 0f, 0.5f);
        this.blockModelRenderer.render(entity.getWorld(), movableModel, entity.getCachedState(), entity.getPos().up(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, Objects.requireNonNull(entity.getWorld()).random, 0, overlay);
        matrices.translate(0f, 0.125f, -1f);
        this.blockModelRenderer.render(entity.getWorld(), woodModel, entity.getCachedState(), entity.getPos().up(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, Objects.requireNonNull(entity.getWorld()).random, 0, overlay);
        matrices.translate(0f, 0f, -1f);
        this.blockModelRenderer.render(entity.getWorld(), woodModel, entity.getCachedState(), entity.getPos().up(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, Objects.requireNonNull(entity.getWorld()).random, 0, overlay);
        matrices.pop();
    }

}
