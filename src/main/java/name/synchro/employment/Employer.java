package name.synchro.employment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface Employer {
    static void employSuitableMob(ServerWorld world, Employer employer, double range) {
        if (range < 0) range = 0;
        Vec3d workingPosition = employer.getWorkerManager().getWorkingPosition();
        List<Entity> entities = world.getOtherEntities(null, Box.of(workingPosition, 1 / 16d, 1 / 16d, 1 / 16d).expand(range),
                entity -> {
            if (entity instanceof PassiveEntity passiveEntity && passiveEntity.getBreedingAge() < 0){
                return false;
            }
            return (entity instanceof Employee employeeMob
                    && !employeeMob.getWorkingHandler().isInEmployment()
                    && employeeMob.getWorkingHandler().availableJob().equals(employer.getWorkerManager().providingJob()));
        });
        Entity target = null;
        double minDistance = range + 114514;
        for (Entity entity: entities){
            double distance = entity.getPos().distanceTo(workingPosition);
            if (distance < minDistance){
                target = entity;
                minDistance = distance;
            }
        }
        if (target != null && ((Employee) target).getWorkingHandler().join(employer)){
            Vec3d targetPos = target.getEyePos();
            Vec3d path = workingPosition.subtract(targetPos);
            double distance = path.length();
            Vec3d pathNormalized = path.normalize();
            for (double i = 0; i < distance; i += 0.125) {
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        targetPos.x + pathNormalized.x * i,
                        targetPos.y + pathNormalized.y * i,
                        targetPos.z + pathNormalized.z * i,
                        1, 0.0, 0.0, 0.0, 0.0);
           }
        }
    }

    static void discardEmployer(ServerWorld serverWorld, Employer employer) {
        for (UUID uuid: new HashSet<>(employer.getWorkerManager().getEmployeesUUID())){
            Entity entity = serverWorld.getEntity(uuid);
            if (entity instanceof Employee employeeMob){
                employeeMob.getWorkingHandler().leave();
            }
        }
        employer.getWorkerManager().removeAllEmployee();
    }

    WorkerManager getWorkerManager();

}
