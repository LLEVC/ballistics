package llevc.ballistics;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FlintlockProjectile extends SmallFireball {
    public FlintlockProjectile(EntityType<? extends SmallFireball> entityType, Level level) {
        super(entityType, level);
    }

    public FlintlockProjectile(Level level, LivingEntity livingEntity, Vec3 vec3) {
        super(level, livingEntity, vec3);
    }

    public FlintlockProjectile(Level level, double d, double e, double f, Vec3 vec3) {
        super(level, d, e, f, vec3);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this,(byte) 3);
            this.kill((ServerLevel) level());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();

            DamageSource damageSource = this.damageSources().source(DamageTypes.ARROW,entity,entity1);
            entity.hurtServer(serverLevel,damageSource,16);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public ItemStack getItem() {
        return ModItems.Ball.getDefaultInstance();
    }
}
