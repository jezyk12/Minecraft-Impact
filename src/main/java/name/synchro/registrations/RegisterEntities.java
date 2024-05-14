package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.entities.DuckEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegisterEntities {
    public static final EntityType<DuckEntity> DUCK = registerMobEntity("duck",
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DuckEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)),
            MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25));

    private static <T extends MobEntity> EntityType<T> registerMobEntity(String path, FabricEntityTypeBuilder<T> entityTypeBuilder, DefaultAttributeContainer.Builder attributeBuilder){
        EntityType<T> self = Registry.register(Registries.ENTITY_TYPE, new Identifier(Synchro.MOD_ID, path), entityTypeBuilder.build());
        FabricDefaultAttributeRegistry.register(self, attributeBuilder);
        return self;
    }

    public static void registerAll(){
        Synchro.LOGGER.debug("Registered all entities for " + Synchro.MOD_ID);
    }
}
