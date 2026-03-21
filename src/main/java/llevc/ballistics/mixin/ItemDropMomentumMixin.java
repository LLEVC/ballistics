package llevc.ballistics.mixin;

import llevc.ballistics.Ballistics;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class ItemDropMomentumMixin {


    @Shadow public abstract @Nullable LivingEntity asLivingEntity();

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
            itemEntity.addDeltaMovement(deltaata);
            Ballistics.LOGGER.info(String.valueOf(deltaata));
            cir.setReturnValue(itemEntity);
        }
    }
}
