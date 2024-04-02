package name.synchro.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import name.synchro.util.BlockEntityExtraCollisionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "HEAD"), argsOnly = true)
    private static List<VoxelShape> addExtraCollisions(List<VoxelShape> collisions,
                                                       @Nullable @Local(argsOnly = true) Entity entity,
                                                       @Local(argsOnly = true) World world) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builder();
        if (entity != null) {
            return builder.addAll(collisions).addAll(BlockEntityExtraCollisionProvider.getBlockEntityExtraCollisions(world, entity)).build();
        }
        else return collisions;
    }
}
