package llevc.ballistics.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Explode extends InstantenousMobEffect {
    public Explode(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int i) {
        return true;
    }

    @Override
    public void applyInstantenousEffect(ServerLevel serverLevel, @Nullable Entity entity, @Nullable Entity entity2, LivingEntity livingEntity, int i, double d) {
        serverLevel.explode(livingEntity, livingEntity.damageSources().explosion(entity,entity2), null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 6.0F, false, Level.ExplosionInteraction.TNT);
        livingEntity.hurtServer(serverLevel,livingEntity.damageSources().explosion(entity,entity2),40);
    }
}
