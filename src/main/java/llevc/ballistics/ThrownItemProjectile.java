package llevc.ballistics;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownItemProjectile extends ThrowableItemProjectile {
    public final float bobOffs;
    public ThrownItemProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
    }

    public ThrownItemProjectile(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level level, ItemStack itemStack) {
        super(type, x, y, z, level, itemStack);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
    }

    public ThrownItemProjectile(EntityType<? extends ThrowableItemProjectile> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
    }

    protected Item getDefaultItem() {
        return Items.ITEM_FRAME;
    }

    public void handleEntityEvent(final @EntityEvent.Value byte id) {
        if (id == 3) {
            ItemStack item = this.getItem();
            ParticleOptions particle = (item.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(item)));

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    private ItemEntity createItemEntity() {
        ItemStack itemStack = getItem();
        ItemEntity bro = new ItemEntity(level(),position().x,position().y,position().z,itemStack);
        ProjectileDeflection.REVERSE.deflect(this,this.getOwner(),random,this.getDeltaMovement());
        bro.setDeltaMovement(this.getDeltaMovement());
        return bro;
    }

    protected void onHitEntity(final EntityHitResult hitResult) {
        if (!level().isClientSide()) {
            ItemStack itemStack = getItem();
            Entity entity = hitResult.getEntity();
            int damage = 1;
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) Math.clamp(Math.ceil(damage * this.getDeltaMovement().length()),0,Integer.MAX_VALUE));
            ItemEntity bro = createItemEntity();
            level().addFreshEntity(bro);
            this.kill((ServerLevel) level());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (!level().isClientSide()) {
            ItemEntity bro = createItemEntity();
            level().addFreshEntity(bro);
            this.kill((ServerLevel) level());
        }
    }

    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        //((ThrowableProjectileInvoker) this).handleFirstTickBubbleColumn();
        if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)0.1F) {
            this.setUnderwaterMovement();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)0.1F) {
            this.setUnderLavaMovement();
        } else {
            this.applyGravity();
        }

        if (this.level().isClientSide()) {
            this.noPhysics = false;
        } else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7));
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / (double)2.0F, this.getZ());
            }
        }

        //((ThrowableProjectileInvoker) this).applyInertia();
        HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        Vec3 newPosition;
        if (result.getType() != HitResult.Type.MISS) {
            //newPosition = result.getLocation();
            setPos(result.getLocation());
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            float airDrag = this.getAirDrag();
            float groundFriction = airDrag;
            if (this.onGround()) {
                groundFriction = airDrag * this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)groundFriction, (double)airDrag, (double)groundFriction));
            if (this.onGround()) {
                Vec3 movement = this.getDeltaMovement();
                if (movement.y < (double)0.0F) {
                    this.setDeltaMovement(movement.multiply((double)1.0F, (double)-0.5F, (double)1.0F));
                }
            }

            //newPosition = this.position().add(this.getDeltaMovement());
        }

        //this.setPos(newPosition);
        //this.updateRotation();
        //this.applyEffectsFromBlocks();

        //super.tick();
        ((ProjectileInterface) this).ballistics$doShootCheck();
        this.checkLeftOwner();
        super.baseTick();
        ((ProjectileInterface) this).ballistics$setLeftOwnerChecked(false);

        if (result.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitTargetOrDeflectSelf(result);
        }

    }

    private void setUnderwaterMovement() {
        this.setFluidMovement((double)0.99F);
    }

    private void setUnderLavaMovement() {
        this.setFluidMovement((double)0.95F);
    }

    private void setFluidMovement(final double multiplier) {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x * multiplier, movement.y + (double)(movement.y < (double)0.06F ? 5.0E-4F : 0.0F), movement.z * multiplier);
    }

    public float getVisualRotationYInDegrees() {
        return 180.0F - ItemEntity.getSpin((float)this.tickCount + 0.5F, this.bobOffs) / ((float)Math.PI * 2F) * 360.0F;
    }
}
