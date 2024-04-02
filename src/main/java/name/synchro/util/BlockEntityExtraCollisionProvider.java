package name.synchro.util;

import name.synchro.SynchroClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface BlockEntityExtraCollisionProvider {
    int MAX_COMPUTE_CHUNK_DISTANCE = 1;
    Collection<VoxelShape> getExtraCollisions();
    static Collection<VoxelShape> getBlockEntityExtraCollisions(WorldView world, Entity entity){
        ChunkPos chunkPos = world.getChunk(entity.getBlockPos()).getPos();
        Set<VoxelShape> extraCollisions = new HashSet<>();
        for (int dx = -MAX_COMPUTE_CHUNK_DISTANCE; dx <= MAX_COMPUTE_CHUNK_DISTANCE; dx++) {
            for (int dz = -MAX_COMPUTE_CHUNK_DISTANCE; dz <= MAX_COMPUTE_CHUNK_DISTANCE; dz++) {
               world.getChunk(chunkPos.x + dx, chunkPos.z + dz).getBlockEntityPositions().forEach((blockPos) -> {
                   if (world.getBlockEntity(blockPos) instanceof BlockEntityExtraCollisionProvider blockEntityExtra){
                       extraCollisions.addAll(blockEntityExtra.getExtraCollisions());
                   }
               });
            }
        }
        return extraCollisions;
    }

    static void displayCollisions(Collection<VoxelShape> collections, MatrixStack matrices, VertexConsumer vertexConsumer){
        if (SynchroClient.displayExtraCollisions){
            collections.forEach((voxelShape) ->
                    WorldRenderer.drawShapeOutline(matrices, vertexConsumer, voxelShape, 0, 0, 0, 0, 1, 0, 1.0f));
        }
    }
}
