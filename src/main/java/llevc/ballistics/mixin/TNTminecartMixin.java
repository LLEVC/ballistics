package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.FlintlockProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecartTNT.class)
public abstract class TNTminecartMixin extends AbstractMinecart {
    protected TNTminecartMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    protected abstract void explode(@Nullable DamageSource damageSource, double speedSqr);

    @WrapMethod(method = "hurtServer")
    public boolean yo(ServerLevel level, DamageSource source, float damage, Operation<Boolean> original) {
        if (source.getDirectEntity() != null) {
            if (source.getDirectEntity() instanceof FlintlockProjectile) {
                DamageSource damageSource = level.damageSources().explosion(this, source.getEntity());
                this.explode(damageSource, source.getDirectEntity().getDeltaMovement().lengthSqr());
            }
        }
        return original.call(level,source,damage);
    }
}
