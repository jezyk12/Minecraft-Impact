package name.synchro.util;

import name.synchro.SynchroClient;
import name.synchro.blockEntities.MillstoneBlockEntity;
import name.synchro.registrations.ModBlockEntities;
import name.synchro.registrations.RegisterPointsOfInterest;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface BlockEntityExtraCollisionProvider {
    int RADIUS = 64;

    Collection<VoxelShape> getExtraCollisions();

    static Collection<VoxelShape> getBlockEntityExtraCollisions(World world, Entity entity){
        Set<VoxelShape> extraCollisions = new HashSet<>();
        if (world instanceof ImportantPoints.Provider view){
            ImportantPoints extraCollisionPoints = view.getImportantPoints().get(ImportantPoints.Type.EXTRA_COLLISION);
            extraCollisionPoints.forEach(blockPos -> {
                if (blockPos.toCenterPos().distanceTo(entity.getPos()) < RADIUS && world.getBlockEntity(blockPos) instanceof BlockEntityExtraCollisionProvider blockEntityExtra){
                    extraCollisions.addAll(blockEntityExtra.getExtraCollisions());
                }
            });
        }
        return extraCollisions;
    }


    static void displayCollisions(Collection<VoxelShape> collections, MatrixStack matrices, VertexConsumer vertexConsumer){
        if (SynchroClient.displayExtraCollisions){
            collections.forEach((voxelShape) ->
                    WorldRenderer.drawShapeOutline(matrices, vertexConsumer, voxelShape, 0, 0, 0, 0, 1, 0, 1.0f));
        }
    }

    @Deprecated
    int MAX_COMPUTE_CHUNK_DISTANCE = 1;
    @Deprecated
    private static @NotNull Set<VoxelShape> getVoxelShapesByOldWay(WorldView world, Entity entity) {
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

    @Deprecated
    private static void getShapesByPointsOfInterest(World world, Entity entity, Set<VoxelShape> extraCollisions) {
        if (world instanceof ServerWorld serverWorld){
            Stream<PointOfInterest> stream = serverWorld.getPointOfInterestStorage().getInCircle(
                    type -> type.value().equals(RegisterPointsOfInterest.MILLSTONE),
                    entity.getBlockPos(), RADIUS, PointOfInterestStorage.OccupationStatus.ANY);
            stream.forEach(point -> {
                Optional<MillstoneBlockEntity> optional = serverWorld.getBlockEntity(point.getPos(), ModBlockEntities.MILLSTONE_BLOCK_ENTITY);
                if (optional.isPresent()){
                    MillstoneBlockEntity millstoneBlockEntity = optional.get();
                    extraCollisions.addAll(millstoneBlockEntity.getExtraCollisions());
                }
            });
        }
    }

}
