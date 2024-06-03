package name.synchro.fluids.gases;

import name.synchro.registrations.BlocksRegistered;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HotSteamGas extends Gas{
    public HotSteamGas() {
        super(0xffffff, 2, 8, 2, 100);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return BlocksRegistered.HOT_STEAM_BLOCK.getDefaultState().with(LEVEL, state.getLevel());
    }

    @Override
    protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        super.onRandomTick(world, pos, state, random);
        spawnSteamParticles(world, pos, random);
    }

    @Override
    protected void onScheduledTick(World world, BlockPos pos, FluidState state) {
        super.onScheduledTick(world, pos, state);
        spawnSteamParticles(world, pos, world.getRandom());
    }

    private void spawnSteamParticles(World world, BlockPos pos, Random random){
        if (world instanceof ServerWorld serverWorld){
            for (int i = 0; i < random.nextBetween(1, 3); i++) {
                serverWorld.spawnParticles(gasParticleEffect, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 1, 0, 0, 0, 0);
            }
        }
    }
}
