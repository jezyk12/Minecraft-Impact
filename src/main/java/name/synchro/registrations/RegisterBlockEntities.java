package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.blockEntities.ElectricLampBlockEntity;
import name.synchro.blockEntities.ElectricSourceBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegisterBlockEntities {
    public static final BlockEntityType<ElectricLampBlockEntity> ELECTRIC_LAMP_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "electric_lamp_entity"),
            FabricBlockEntityTypeBuilder.create(ElectricLampBlockEntity::new, RegisterBlocks.ELECTRIC_LAMP).build());
    public static final BlockEntityType<ElectricSourceBlockEntity> ELECTRIC_SOURCE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "source_block_entity"),
            FabricBlockEntityTypeBuilder.create(ElectricSourceBlockEntity::new, RegisterBlocks.ELECTRIC_SOURCE).build());
    public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod block entities for" + Synchro.MOD_ID);
    }
}
