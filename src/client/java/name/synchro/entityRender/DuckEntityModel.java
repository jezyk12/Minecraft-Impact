package name.synchro.entityRender;

import name.synchro.entities.DuckEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class DuckEntityModel extends EntityModel<DuckEntity> {
    private final ModelPart duck;
    public DuckEntityModel(ModelPart root) {
        this.duck = root.getChild("duck");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData duck = modelPartData.addChild("duck", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.0F, -2.0F, 6.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-2.0F, -10.0F, -4.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(12, 12).cuboid(-2.0F, -8.0F, -7.0F, 4.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(16, 16).cuboid(-2.0F, -7.0F, -2.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 2).cuboid(-2.0F, -2.0F, 2.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(1.0F, -2.0F, 2.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 2).cuboid(-2.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        duck.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public void setAngles(DuckEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
