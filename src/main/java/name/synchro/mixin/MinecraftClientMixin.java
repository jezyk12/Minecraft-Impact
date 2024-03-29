package name.synchro.mixin;

import name.synchro.mixinHelper.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique
    private Entity focusedEntity;
    @Unique
    private HitResult focusingResult;
    @Override
    public Entity getFocusedEntity() {
        return this.focusedEntity;
    }

    @Override
    public void setFocusedEntity(Entity entity) {
        this.focusedEntity = entity;
    }

    @Override
    public HitResult getFocusingResult() {
        return focusingResult;
    }

    @Override
    public void setFocusingResult(HitResult hitResult) {
        this.focusingResult = hitResult;
    }
}
