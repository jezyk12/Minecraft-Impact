package name.synchro.modUtilData.reactions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import name.synchro.Synchro;
import name.synchro.registrations.ModRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LocationAction {
    Codec<LocationAction> CODEC = ModRegistries.LOCATION_ACTION_TYPE.getCodec().dispatch("type", LocationAction::getType, Type::codec);
    Type<?> getType();
    void act(World world, BlockPos blockPos);
    Type<SetBlock> SET_BLOCK = Registry.register(ModRegistries.LOCATION_ACTION_TYPE,
            Identifier.of(Synchro.MOD_ID, "set_block"), new Type<>(SetBlock.CODEC));
    Type<SetFluid> SET_FLUID = Registry.register(ModRegistries.LOCATION_ACTION_TYPE,
            Identifier.of(Synchro.MOD_ID, "set_fluid"), new Type<>(SetFluid.CODEC));
    Type<DropItems> DROP_ITEMS = Registry.register(ModRegistries.LOCATION_ACTION_TYPE,
            Identifier.of(Synchro.MOD_ID, "drop_items"), new Type<>(DropItems.CODEC));
    Type<ActiveEvents> ACTIVE_EVENTS = Registry.register(ModRegistries.LOCATION_ACTION_TYPE,
            Identifier.of(Synchro.MOD_ID, "active_events"), new Type<>(ActiveEvents.CODEC));
    record Type<T extends LocationAction>(MapCodec<T> codec) {}
    static void registerAll(){
        Synchro.LOGGER.debug("Registered location actions for " + Synchro.MOD_ID);
    }

}
