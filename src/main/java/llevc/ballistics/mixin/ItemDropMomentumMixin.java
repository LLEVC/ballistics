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
                    ThrownItemProjectile.giveCorrectProjectile(itemStack,serverLevel,bih,itemEntity.position(),newVelo,true);
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
