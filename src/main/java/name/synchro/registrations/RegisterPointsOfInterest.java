package name.synchro.registrations;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

@Deprecated
public final class RegisterPointsOfInterest {
    public static final PointOfInterestType MILLSTONE = PointOfInterestHelper.register(Identifier.of(Synchro.MOD_ID, "millstone"), 1, 1, ModBlocks.MILLSTONE);
    public static void registerAll(){
        Synchro.LOGGER.debug("Registered all points of interest for" + Synchro.MOD_ID);
    }
}
