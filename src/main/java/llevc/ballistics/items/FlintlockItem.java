package llevc.ballistics.items;

import llevc.ballistics.FlintlockProjectile;
import llevc.ballistics.ModItems;
import llevc.ballistics.ModSounds;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FlintlockItem extends ProjectileWeaponItem {
    public FlintlockItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 16;
    }

    @Override //bow code as placeholder
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float f, float g, float h, @Nullable LivingEntity livingEntity2) {
        projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot() + h, 0.0F, f, g);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (isCharged(itemStack)) {
            setCharged(itemStack, false);

            if (isLoaded(itemStack)) {
                setLoaded(itemStack,false);
                if (level instanceof ServerLevel serverLevel) {
                    this.shoot(serverLevel, player, player.getUsedItemHand(), itemStack, List.of(new ItemStack(ModItems.Ball)), 10.0F, 1.0F, true, null);
                }
                //level.playSound(player,player.getX(),player.getY(),player.getZ(), ModSounds.FlintlockShoot, getSoundSource(player));
                return InteractionResult.SUCCESS;
            } else {
                //level.playSound(player,player.getX(),player.getY(),player.getZ(), ModSounds.FlintlockDryShoot, getSoundSource(player));
                return InteractionResult.PASS;
            }
        } else {
            player.startUsingItem(interactionHand);
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        //Ballistics.LOGGER.info(String.valueOf(i));
        int hey = getUseDuration(itemStack,livingEntity) - i;
        if (hey > 20 && !isCharged(itemStack)) {
            setCharged(itemStack,true);
            //level.playSound(livingEntity,livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(), ModSounds.FlintlockClick, getSoundSource(livingEntity));
        }
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        return super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }

    public boolean isCharged(ItemStack itemStack) {
        CustomData yo = itemStack.get(DataComponents.CUSTOM_DATA);
        if (yo == null) {
            return false;
        }
        if (yo.isEmpty()) {
            return false;
        }
        if (yo.copyTag().getBoolean("charged").isPresent()) {
            return yo.copyTag().getBoolean("charged").get().booleanValue();
        }
        return false;
    }

    public void setCharged(ItemStack itemStack, boolean bool) {
        CustomData yo = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag heyo = new CompoundTag();
        if (yo != null) {
            if (!yo.isEmpty()){
                heyo = yo.copyTag();
            }
        }
        heyo.remove("charged");
        heyo.putBoolean("charged", bool);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(heyo));
    }

    public boolean isLoaded(ItemStack itemStack) {
        ChargedProjectiles yo = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
        return !yo.isEmpty();
    }

    public void setLoaded(ItemStack itemStack, boolean bool) {
        ChargedProjectiles yo = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
        if (bool) {
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(ModItems.Ball)));
        } else {
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }
    }

    public SoundSource getSoundSource(LivingEntity livingEntity) {
        SoundSource soundSource = SoundSource.HOSTILE;
        if (livingEntity instanceof Player) {
            soundSource = SoundSource.PLAYERS;
        }
        return soundSource;
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl) {
        FlintlockProjectile flintlockProjectile = new FlintlockProjectile(level,livingEntity, Vec3.ZERO);
        flintlockProjectile.setPos(livingEntity.getEyePosition());
        return flintlockProjectile;
    }
}
