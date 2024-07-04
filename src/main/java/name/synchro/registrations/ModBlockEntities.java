package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.blockEntities.*;
import name.synchro.blocks.Rock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
    public static final BlockEntityType<ElectricLampBlockEntity> ELECTRIC_LAMP_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "electric_lamp_entity"),
            FabricBlockEntityTypeBuilder.create(ElectricLampBlockEntity::new, ModBlocks.ELECTRIC_LAMP).build());
    public static final BlockEntityType<ElectricSourceBlockEntity> ELECTRIC_SOURCE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "source_block_entity"),
            FabricBlockEntityTypeBuilder.create(ElectricSourceBlockEntity::new, ModBlocks.ELECTRIC_SOURCE).build());
    public static final BlockEntityType<DebugBlockEntity> DEBUG_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "debug_block_entity"),
            FabricBlockEntityTypeBuilder.create(DebugBlockEntity::new, ModBlocks.DEBUG_BLOCK).build());
   public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "millstone_block_entity"),
            FabricBlockEntityTypeBuilder.create(MillstoneBlockEntity::new, ModBlocks.MILLSTONE).build());
   public static final BlockEntityType<StrawNestBlockEntity> STRAW_NEST_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "straw_nest_block_entity"),
            FabricBlockEntityTypeBuilder.create(StrawNestBlockEntity::new, ModBlocks.STRAW_NEST).build());
   public static final BlockEntityType<Rock.MutableBlockEntity> MUTABLE_ROCK_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "mutable_rock_block_entity"),
            FabricBlockEntityTypeBuilder.create(Rock.MutableBlockEntity::new, ModBlocks.STRAW_NEST).build());
   public static final BlockEntityType<PointerBlockEntity> POINTER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "pointer_block_entity"),
            FabricBlockEntityTypeBuilder.create(PointerBlockEntity::new, ModBlocks.STRAW_NEST).build());
   public static final BlockEntityType<ComplexLiquidBlockEntity> COMPLEX_LIQUID_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Synchro.MOD_ID, "complex_liquid_block_entity"),
            FabricBlockEntityTypeBuilder.create(ComplexLiquidBlockEntity::new, ModBlocks.COMPLEX_LIQUID).build());

   public static void registerAll() {
        Synchro.LOGGER.debug("Registered mod block entities for" + Synchro.MOD_ID);
    }
}
