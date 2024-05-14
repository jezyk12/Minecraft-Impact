package name.synchro.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import name.synchro.blocks.AbstractFarmlandBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(FarmerVillagerTask.class)
public class FarmerVillagerTaskMixin {
	@WrapOperation
			(method = "isSuitableTarget",
			constant = @Constant(classValue = FarmlandBlock.class))
	private boolean isSuitableTargetMixin(Object object, Operation<Boolean> original){
		if (object instanceof AbstractFarmlandBlock){
			return true;
		}
		else{
			return original.call(object);
		}
	}
	@WrapOperation
			(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;J)V",
			constant = @Constant(classValue = FarmlandBlock.class))
	private boolean keepRunningMixin(Object object, Operation<Boolean> original){
		if (object instanceof AbstractFarmlandBlock){
			return true;
		}
		else{
			return original.call(object);
		}
	}
}