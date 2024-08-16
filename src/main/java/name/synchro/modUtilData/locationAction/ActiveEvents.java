package name.synchro.modUtilData.locationAction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public record ActiveEvents(List<Pair<Integer, Integer>> events) implements LocationAction{
    private static final Codec<Pair<Integer, Integer>> PAIR_CODEC = RecordCodecBuilder.<Pair<Integer, Integer>>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("event_id").forGetter(Pair::getFirst),
            Codec.INT.optionalFieldOf("data", 0).forGetter(Pair::getSecond)
    ).apply(instance, Pair::new)).codec();

    public static final MapCodec<ActiveEvents> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.list(PAIR_CODEC).fieldOf("events").forGetter(ActiveEvents::events)
            ).apply(ins, ActiveEvents::new));

    @Override
    public Type<?> getType() {
        return LocationAction.ACTIVE_EVENTS;
    }

    @Override
    public void act(World world, BlockPos blockPos) {
        for (Pair<Integer, Integer> entry : events()){
            world.syncWorldEvent(entry.getFirst(), blockPos, entry.getSecond());
        }
    }
}
