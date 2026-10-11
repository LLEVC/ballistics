package llevc.ballistics.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractHurtingProjectile.class)
public abstract class FixParticleTrailPls {
    @Shadow
    protected abstract @Nullable ParticleOptions getTrailParticle();

    @Redirect(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/AbstractHurtingProjectile;createParticleTrail()V"))
    public void bro(AbstractHurtingProjectile instance) {
        ParticleOptions trailParticle = this.getTrailParticle();
        Vec3 position = instance.position();
        if (trailParticle != null) {
            instance.level().addParticle(trailParticle, position.x, position.y + instance.getBbHeight()/2, position.z, 0, 0, 0);
        }
    }
}
