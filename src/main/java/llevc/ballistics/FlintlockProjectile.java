package llevc.ballistics;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
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
            level().playSound(null,blockHitResult.getBlockPos(),ModSounds.FlintlockMiss, SoundSource.PLAYERS,0.5f,1.0f);
            this.kill((ServerLevel) level());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();

            DamageSource damageSource = this.damageSources().mobProjectile(this,entity1.asLivingEntity());
            serverLevel.playSound(null,entityHitResult.getEntity().blockPosition(),ModSounds.FlintlockHit, SoundSource.PLAYERS,0.5f,1.0f);
            entity.hurtServer(serverLevel,damageSource,16);
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isBlocking()) {
                    BlocksAttacks blocksAttacks = livingEntity.getItemBlockingWith().get(DataComponents.BLOCKS_ATTACKS);
                    if (blocksAttacks != null) {
                        blocksAttacks.disable(serverLevel,livingEntity,5.0f,livingEntity.getItemBlockingWith());
                    }
                }
            }
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
