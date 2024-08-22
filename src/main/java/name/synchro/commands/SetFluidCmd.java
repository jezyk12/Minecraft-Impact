package name.synchro.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import name.synchro.fluids.FluidHelper;
import name.synchro.fluids.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public final class SetFluidCmd {
    public static void registerSetFluidCmd(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("setfluid")
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .then(CommandManager.argument("fluid", new FluidStateArgType(registryAccess))
                                .requires(s -> s.hasPermissionLevel(2))
                                .executes(SetFluidCmd::runSetFluidCmd)
                                .then(CommandManager.argument("forced", BoolArgumentType.bool())
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .executes(SetFluidCmd::runSetFluidCmdForced)))));
    }

    private static int runSetFluidCmd(CommandContext<ServerCommandSource> context) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        FluidState fluid = context.getArgument("fluid", FluidState.class);
        return setFluidState(context.getSource().getWorld(), pos, fluid, context.getSource(), false);
    }

    private static int runSetFluidCmdForced(CommandContext<ServerCommandSource> context) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        FluidState fluid = context.getArgument("fluid", FluidState.class);
        boolean forced = BoolArgumentType.getBool(context, "forced");
        return setFluidState(context.getSource().getWorld(), pos, fluid, context.getSource(), forced);
    }

    private static int setFluidState(ServerWorld world, BlockPos pos, FluidState state, ServerCommandSource source, boolean forced){
        BlockState blockState = world.getBlockState(pos);
        if (!forced && !state.isEmpty() && FluidUtil.canBlockReplaceFluid(world, pos, blockState, state.getHeight())) {
            String blockName = blockState.getBlock().getName().getString();
            source.sendError(Text.stringifiedTranslatable("commands.synchro.fluid.blocked", blockName));
            return -1;
        }
        if (((FluidHelper.ForWorld) world).synchro$setFluidState(pos, state)) {
            source.sendFeedback(() -> Text.translatable("commands.synchro.fluid.set"), false);
            return 1;
        }
        source.sendError(Text.translatable("commands.synchro.fluid.unsuccessful"));
        return -1;
    }
}
