package name.synchro.mixin.client;

import name.synchro.util.FocusingProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements FocusingProvider {
    @Unique
    private Entity focusedEntity;
    @Unique
    private HitResult focusingResult;

    @Override
    public Entity synchro$getFocusedEntity() {
        return this.focusedEntity;
    }

    @Override
    public void synchro$setFocusedEntity(Entity entity) {
        this.focusedEntity = entity;
    }

    @Override
    public HitResult synchro$getFocusingResult() {
        return focusingResult;
    }

    @Override
    public void synchro$setFocusingResult(HitResult hitResult) {
        this.focusingResult = hitResult;
    }

}
