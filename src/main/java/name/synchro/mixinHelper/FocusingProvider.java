package name.synchro.mixinHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public interface FocusingProvider {
    Entity synchro$getFocusedEntity();
    void synchro$setFocusedEntity(Entity entity);
    HitResult synchro$getFocusingResult();
    void synchro$setFocusingResult(HitResult hitResult);
}
