package name.synchro.blockEntityRenderers;

import name.synchro.blockEntities.ComplexLiquidBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ComplexLiquidRenderer implements BlockEntityRenderer<ComplexLiquidBlockEntity> {
    @Override
    public void render(ComplexLiquidBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }

    @Override
    public boolean rendersOutsideBoundingBox(ComplexLiquidBlockEntity blockEntity) {
        return true;
    }
}
