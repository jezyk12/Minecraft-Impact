package name.synchro.util;

import name.synchro.SynchroClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShape;

import java.util.Collection;

public final class BlockEntityExtraCollisionDisplay {
    public static void displayCollisions(Collection<VoxelShape> collections, MatrixStack matrices, VertexConsumer vertexConsumer){
        if (SynchroClient.displayExtraCollisions){
            collections.forEach((voxelShape) ->
                    WorldRenderer.drawShapeOutline(matrices, vertexConsumer, voxelShape, 0, 0, 0, 0f, 0f, 1f, 1f, true));
        }
    }
}
