package llevc.ballistics;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FlintlockProjectile extends AbstractHurtingProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> dataItem;
    
    public FlintlockProjectile(EntityType<FlintlockProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlintlockProjectile(final Level level, final LivingEntity mob, final Vec3 direction) {
        super(ModEntities.flintlockProjectile, mob, direction, level);
    }

    public FlintlockProjectile(final Level level, final double x, final double y, final double z, final Vec3 direction) {
        super(ModEntities.flintlockProjectile, x, y, z, direction, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if (!level().isClientSide()) {
            level().broadcastEntityEvent(this,(byte) 3);
            level().playSound(null,blockHitResult.getBlockPos(),ModSounds.FlintlockMiss, SoundSource.PLAYERS,2.0f,1.0f);
            this.kill((ServerLevel) level());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();

            DamageSource damageSource = this.damageSources().mobProjectile(this,entity1.asLivingEntity());
            serverLevel.playSound(null,entityHitResult.getEntity().blockPosition(),ModSounds.FlintlockHit, entity1.getSoundSource(),2.0f,1.0f);
            entity.hurtServer(serverLevel,damageSource,16);
            if (this.isOnFire()) {
                entity.setRemainingFireTicks(entity.getRemainingFireTicks()+40);
            }
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isBlocking()) {
                    BlocksAttacks blocksAttacks = livingEntity.getItemBlockingWith().get(DataComponents.BLOCKS_ATTACKS);
                    if (blocksAttacks != null) {
                        blocksAttacks.disable(serverLevel,livingEntity,5.0f,livingEntity.getItemBlockingWith());
                    }
                }
            }
            this.kill(serverLevel);
        }
    }

    protected boolean canHitEntity(final Entity entity) {
        return super.canHitEntity(entity);
    }

    protected @Nullable ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    // item data shtuff
    public @NonNull ItemStack getItem() {
        return this.getEntityData().get(dataItem);
    }

    public static ItemStack defaultItem = ModItems.Ball.getDefaultInstance();

    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        entityData.define(dataItem, defaultItem);
    }

    protected void addAdditionalSaveData(final ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("Item", ItemStack.CODEC, this.getItem());
    }

    protected void readAdditionalSaveData(final ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setItem(input.read("Item", ItemStack.CODEC).orElse(defaultItem));
    }

    public void setItem(final ItemStack source) {
        if (source.isEmpty()) {
            this.getEntityData().set(dataItem, defaultItem);
        } else {
            this.getEntityData().set(dataItem, source.copyWithCount(1));
        }
    }

    @Override
    public boolean fireImmune() {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    static {
        dataItem = SynchedEntityData.defineId(FlintlockProjectile.class, EntityDataSerializers.ITEM_STACK);
    }
}
