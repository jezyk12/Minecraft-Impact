package name.synchro.blockEntityRenderers;

import name.synchro.blockEntities.DebugBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class DebugBlockEntityRenderer implements BlockEntityRenderer<DebugBlockEntity> {
    public DebugBlockEntityRenderer(BlockEntityRendererFactory.Context context){}
    @Override
    public void render(DebugBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        float deg = (Objects.requireNonNull(entity.getWorld()).getTime() + tickDelta);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deg), 0.5f, 0f, 0.5f);
        matrices.translate(0, 1, 0);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(Blocks.SMOOTH_STONE_SLAB.getDefaultState(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, Random.create());
        matrices.pop();
    }
}
