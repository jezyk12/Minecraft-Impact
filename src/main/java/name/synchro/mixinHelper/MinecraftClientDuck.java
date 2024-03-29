package name.synchro.mixinHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public interface MinecraftClientDuck {
    Entity getFocusedEntity();
    void setFocusedEntity(Entity entity);
    HitResult getFocusingResult();
    void setFocusingResult(HitResult hitResult);
}
