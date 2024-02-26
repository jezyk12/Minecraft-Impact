package name.synchro.mixin;

import name.synchro.mixinHelper.PlayerEntityDuck;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityDuck {
    @Unique private int theFireTicks;

    @Override
    public void setTheFireTicks(int value) {
        this.theFireTicks = value;
    }

    @Override
    public int getTheFireTicks() {
        return theFireTicks;
    }
}
