package name.synchro.modUtilData.locationAction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import name.synchro.fluids.FluidHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public record SetFluid(FluidState fluidState, boolean followProperties) implements LocationAction {
    public static final MapCodec<SetFluid> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    FluidState.CODEC.fieldOf("fluid").forGetter(SetFluid::fluidState),
            Codec.BOOL.optionalFieldOf("follow_properties", false).forGetter(SetFluid::followProperties))
            .apply(instance, SetFluid::new));

    @Override
    public Type<?> getType() {
        return SET_FLUID;
    }

    @Override
    public void act(World world, BlockPos blockPos) {
        FluidState toSetState = fluidState();
        if (followProperties()){
            try{
                FluidState originalState = world.getFluidState(blockPos);
                Collection<Property<?>> properties = toSetState.getProperties();
                for (Property property : originalState.getProperties()) {
                    if (properties.contains(property)) {
                        toSetState = toSetState.with(property, originalState.get(property));
                    }
                }
            }catch (Exception e){
                toSetState = fluidState();
                Synchro.LOGGER.error("Exception occurred when applying fluidState properties at {} with {}", blockPos, e);
            }
        }
        ((FluidHelper.ForWorld) world).synchro$setFluidState(blockPos, toSetState);
    }
}
