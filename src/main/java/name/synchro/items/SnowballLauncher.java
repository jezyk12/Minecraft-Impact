package name.synchro.items;

import name.synchro.registrations.ItemsRegistered;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class SnowballLauncher extends Item {
    public SnowballLauncher(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 128;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks % 2 == 1){
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (!world.isClient) {
                SnowballEntity snowball = new SnowballEntity(world, user);
                snowball.setItem(new ItemStack(Items.SNOWBALL));
                snowball.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
                world.spawnEntity(snowball);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player){
            player.getItemCooldownManager().set(ItemsRegistered.SNOWBALL_LAUNCHER, 128 - remainingUseTicks);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player){
            player.getItemCooldownManager().set(ItemsRegistered.SNOWBALL_LAUNCHER, 128);
        }
        return stack;
    }

}
