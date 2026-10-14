package llevc.ballistics.mixin;

import llevc.ballistics.*;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class ItemDropMomentumMixin {
    @Shadow public abstract @Nullable LivingEntity asLivingEntity();

    @Shadow
    public abstract @org.jspecify.annotations.Nullable LivingEntity getKillCredit();

    @Inject(at=@At("RETURN"),method="createItemStackToDrop", cancellable = true)
    public void init(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity itemEntity = cir.getReturnValue();
        LivingEntity bih = Objects.requireNonNull(this.asLivingEntity());
        Vec3 deltaata = bih.getKnownMovement();
        if (bih.isFallFlying()) {
            deltaata = deltaata.add(bih.getDeltaMovement().multiply(0.5,0.5,0.5));
        }
        if (itemEntity != null) {
            Vec3 ogVelo = itemEntity.getDeltaMovement();
            boolean turnToProjectile = false;
            if (bih instanceof Player player) {
                if (((PlayerInterface) player).ballistics$getUseThrowCharge()) {
                    float bro = (float) Math.clamp((player.tickCount-((PlayerInterface) player).ballistics$getThrowChargeTime())/38f,0f,1.2f);
                    //Ballistics.LOGGER.info(String.valueOf(bro));
                    turnToProjectile = bro >= 1.0f;
                    deltaata = deltaata.add(ogVelo.multiply(bro, bro, bro));
                    ((PlayerInterface) player).ballistics$resetThrowCharge();
                }
            }
            itemEntity.addDeltaMovement(deltaata);
            Vec3 newVelo = itemEntity.getDeltaMovement();
            if (turnToProjectile) {
                if (bih.level() instanceof ServerLevel serverLevel) {
                    boolean isFirework = itemStack.getItem() instanceof FireworkRocketItem;
                    if (itemStack.getItem() instanceof ProjectileItem projectileItem && !itemStack.is(ModItems.projectileThrowBlacklist) && (!(isFirework) || (bih.isHolding(ModItems.rhGlove) && isFirework))) {
                        Projectile projectile = projectileItem.asProjectile(serverLevel,itemEntity.position(),itemStack, Direction.NORTH);
                        //Vec3 viewVector = bih.getViewVector(1.0f);
                        //projectile.setOwner(bih);
                        projectile.shootFromRotation(bih,bih.getXRot(),bih.getYRot(),0f,1.5f,1f);
                        Projectile.spawnProjectile(projectile,serverLevel,itemStack);
                    } else if (itemStack.getItem() instanceof TridentItem) {
                        ItemStack returnItemStack = itemStack.consumeAndReturn(1,bih);
                        Projectile.spawnProjectileFromRotation(ThrownTrident::new,serverLevel,returnItemStack,bih,0,2.5f,1f);
                    } else {
                        ThrownItemProjectile thrownItemProjectile = new ThrownItemProjectile(ModEntities.thrownItemProjectile, bih, bih.level(), itemStack);
                        //thrownItemProjectile.shoot(newVelo.x,newVelo.y,newVelo.z,1.0f,0.0f);
                        thrownItemProjectile.setDeltaMovement(newVelo);
                        thrownItemProjectile.setPos(itemEntity.position());
                        Projectile.spawnProjectile(thrownItemProjectile, serverLevel, itemStack);
                        //bih.level().addFreshEntity(thrownItemProjectile);
                    }
                }
                itemEntity = null;
            }
            //Ballistics.LOGGER.info(String.valueOf(deltaata));
            cir.setReturnValue(itemEntity);
        }
    }
}
