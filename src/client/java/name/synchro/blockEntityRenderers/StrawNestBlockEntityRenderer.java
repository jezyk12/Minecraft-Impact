package name.synchro.blockEntityRenderers;

import name.synchro.blockEntities.StrawNestBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class StrawNestBlockEntityRenderer implements BlockEntityRenderer<StrawNestBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockEntityRenderDispatcher dispatcher;
    public StrawNestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.dispatcher = context.getRenderDispatcher();
    }

    @Override
    public void render(StrawNestBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Vec3d cameraPos = dispatcher.camera.getPos();
        Vec3d blockCenter = new Vec3d(blockEntity.getPos().getX() + 0.5, blockEntity.getPos().getY(), blockEntity.getPos().getZ() + 0.5);
        Vec3d cameraOffset = cameraPos.subtract(blockCenter);
        double rotY = Math.atan2(cameraOffset.x, cameraOffset.z);
        matrices.push();
        matrices.translate(0.5, 0.3, 0.5);
        ItemStack stack = blockEntity.getStack(0);
        int displayCount = Math.min((stack.getCount() + 3) >> 2, 4);
        double offset = switch (displayCount) {
            case 0, 1 -> 0;
            case 2 -> 0.15;
            case 3 -> 0.18;
            default -> 0.2;
        };
        for (int i = 0; i < displayCount; i++) {
            double dx = Math.sin(2 * Math.PI * ((double) i / displayCount + 0.0625)) * offset;
            double dz = Math.cos(2 * Math.PI * ((double) i / displayCount + 0.0625)) * offset;
            matrices.push();
            matrices.translate(dx, 0, dz);
            matrices.multiply(new Quaternionf().rotateY((float) rotY));
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
            matrices.pop();
        }
        matrices.pop();
    }
}
