package llevc.ballistics.mixin;

import llevc.ballistics.*;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
            int overcharge = 0;
            if (bih instanceof Player player) {
                if (((PlayerInterface) player).ballistics$getUseThrowCharge()) {
                    int maxOvercharge = ((PlayerInterface) player).ballistics$getMaxOvercharge();
                    float progress = (player.tickCount-((PlayerInterface) player).ballistics$getThrowChargeTime())/((float) ((PlayerInterface) player).ballistics$getMaxChargeTime());
                    float progressClamped = Math.clamp(progress,0f,1.0f);
                    float progressClampedToOvercharge = Math.clamp(progress,0f,maxOvercharge+1f);
                    boolean overchargeAsPower = EnchantmentHelper.has(player.getOffhandItem().isEmpty() ? itemStack : player.getOffhandItem(), ModEnchantmentEffects.overchargeAsPower);
                    float power = BallisticsEnchantHelper.getThrowPower(itemStack,player,overchargeAsPower ? progressClampedToOvercharge : progressClamped);
                    if (!player.getOffhandItem().isEmpty()) {
                        float scro = BallisticsEnchantHelper.getThrowPower(player.getOffhandItem(), player, power);
                        if (scro != power) power = scro;
                    }
                    //Ballistics.LOGGER.info(String.valueOf(bro));
                    overcharge = (int) Math.clamp(Math.floor(progress-1f),0,maxOvercharge);
                    if (overcharge > 0 && !overchargeAsPower) {
                        if (!player.getMainHandItem().isEmpty()) {
                            int theCheck = player.getMainHandItem().getCount() - overcharge;
                            //Ballistics.LOGGER.info(String.valueOf(overcharge));
                            //Ballistics.LOGGER.info(String.valueOf(theCheck));
                            if (theCheck >= 0) {
                                player.getMainHandItem().shrink(overcharge);
                            } else {
                                overcharge = 0;
                            }
                        } else {
                            overcharge = 0;
                        }
                    }
                    turnToProjectile = progressClamped >= 0.9f;
                    deltaata = deltaata.add(ogVelo.multiply(power, power, power));
                    if (overchargeAsPower) overcharge = 0;
                    ((PlayerInterface) player).ballistics$resetThrowCharge();
                }
            }
            itemEntity.addDeltaMovement(deltaata);
            Vec3 newVelo = itemEntity.getDeltaMovement();
            if (turnToProjectile) {
                if (bih.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < overcharge+1; i++) {
                        ThrownItemProjectile.giveCorrectProjectile(itemStack.copyWithCount(1),serverLevel,bih,itemEntity.position(),newVelo,true);
                    }
                }
                itemEntity = null;
            }
            //Ballistics.LOGGER.info(String.valueOf(deltaata));
            cir.setReturnValue(itemEntity);
        }
    }

    @Unique
    private boolean isFallingBlock(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof FallingBlock;
        }
        return false;
    }
}
