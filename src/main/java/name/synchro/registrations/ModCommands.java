package name.synchro.registrations;

import name.synchro.Synchro;
import name.synchro.commands.FluidStateArgType;
import name.synchro.commands.SetFluidCmd;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public final class ModCommands {
    public static void registerAll(){
        ArgumentTypeRegistry.registerArgumentType(Synchro.id("fluid_state_arg"), FluidStateArgType.class, ConstantArgumentSerializer.of(FluidStateArgType::new));
        CommandRegistrationCallback.EVENT.register(SetFluidCmd::registerSetFluidCmd);
        Synchro.LOGGER.debug("Registered commands for " + Synchro.MOD_ID);
    }

}
