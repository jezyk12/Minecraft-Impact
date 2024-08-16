package name.synchro.modUtilData.locationAction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public record SetBlock(BlockState blockState, boolean destroyEffect, boolean followProperties) implements LocationAction {
    public static final MapCodec<SetBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockState.CODEC.fieldOf("block").forGetter(SetBlock::blockState),
                    Codec.BOOL.optionalFieldOf("destroy_effect", false).forGetter(SetBlock::destroyEffect),
                    Codec.BOOL.optionalFieldOf("follow_properties", false).forGetter(SetBlock::followProperties)
            ).apply(instance, SetBlock::new));

    @Override
    public Type<?> getType() {
        return SET_BLOCK;
    }

    @Override
    public void act(World world, BlockPos blockPos) {
        BlockState toSetState = blockState();
        try {
            if (followProperties()) {
                BlockState originalState = world.getBlockState(blockPos);
                Collection<Property<?>> properties = toSetState.getProperties();
                for (Property property : originalState.getProperties()) {
                    if (properties.contains(property)) {
                        toSetState = toSetState.with(property, originalState.get(property));
                    }
                }
            }
        }catch (Exception e){
            toSetState = blockState();
            Synchro.LOGGER.warn("Ignored argument follow_properties at {} due to {}", blockPos, e);
        }
        if (destroyEffect()) {
            world.breakBlock(blockPos, false);
        }
        world.setBlockState(blockPos, toSetState);
    }

    public static Builder builder(BlockState state){
        return new Builder(state);
    }

    public static class Builder{
        private final BlockState state;
        private boolean destroyEffect = false;
        private boolean followProperties = false;

        private Builder(BlockState state){
            this.state = state;
        }

        public Builder destroyEffect(){
            this.destroyEffect = true;
            return this;
        }

        public Builder followProperties(){
            this.followProperties = true;
            return this;
        }

        public SetBlock build(){
            return new SetBlock(state, destroyEffect, followProperties);
        }
    }

}
