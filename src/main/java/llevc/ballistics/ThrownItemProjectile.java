package llevc.ballistics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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

    private ItemEntity createItemEntity(ItemStack itemStack) {
        ItemEntity bro = new ItemEntity(level(),position().x,position().y,position().z,itemStack);
        ProjectileDeflection.REVERSE.deflect(this,this.getOwner(),random,this.getDeltaMovement());
        bro.setDeltaMovement(this.getDeltaMovement());
        return bro;
    }

    private void spawnItemEntity(ItemStack itemStack) {
        ItemEntity bro = createItemEntity(itemStack);
        level().addFreshEntity(bro);
    }

    private void spawnItemEntity() {
        ItemEntity bro = createItemEntity(getItem());
        level().addFreshEntity(bro);
    }

    private boolean fireworkExplosion() {
        ItemStack itemStack = getItem();
        if (itemStack.getItem() instanceof FireworkRocketItem) {
            ItemStack itemStack1 = itemStack.copy();
            Fireworks fireworks = itemStack.getOrDefault(DataComponents.FIREWORKS,new Fireworks(1, List.of()));
            itemStack1.set(DataComponents.FIREWORKS,new Fireworks(-2,fireworks.explosions()));
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(level(),itemStack1,getX(),getY(),getZ(),true);
            fireworkRocketEntity.setOwner(getOwner());
            fireworkRocketEntity.shoot(0,0,0,1.6f,0f);
            Projectile.spawnProjectile(fireworkRocketEntity,((ServerLevel)level()),itemStack1);
            return true;
        }
        return false;
    }

    private boolean placeFluid(BlockPos pos, @Nullable Direction direction) {
        ItemStack itemStack = getItem();
        if (itemStack.getItem() instanceof DispensibleContainerItem bucketItem) {
            Vec3i off = direction != null ? direction.getStep() : Vec3i.ZERO;
            bucketItem.emptyContents((LivingEntity) getOwner(),level(),pos.offset(off),null);
            bucketItem.checkExtraContent((LivingEntity) getOwner(),level(),itemStack,pos.offset(off));
            //Ballistics.LOGGER.info(String.valueOf(pos.offset(off)));
            spawnItemEntity(new ItemStack(Items.BUCKET));
            return true;
        }
        return false;
    }

    private Rotation getDirectionRotation(Direction direction) {
        Rotation rotation = Rotation.NONE;
        switch (direction) {
            //case NORTH -> rotation = Rotation.NONE;
            case EAST -> rotation = Rotation.CLOCKWISE_90;
            case SOUTH -> rotation = Rotation.CLOCKWISE_180;
            case WEST -> rotation = Rotation.COUNTERCLOCKWISE_90;
        }
        return rotation;
    }

    private boolean isTorch() {
        ItemStack itemStack = getItem();
        if (itemStack.getItem() instanceof StandingAndWallBlockItem blockItem) {
            return blockItem.getBlock() instanceof BaseTorchBlock;
        }
        return false;
    }

    private boolean placeTorch(BlockPos pos, Direction direction) {
        ItemStack itemStack = getItem();
        if (isTorch() && direction != Direction.DOWN) {
            BlockItem blockItem = (BlockItem) itemStack.getItem();
            Vec3i off = direction.getStep();
            level().destroyBlock(pos.offset(off),true);
            level().setBlockAndUpdate(pos.offset(off),direction == Direction.UP ? blockItem.getBlock().defaultBlockState() : ((StandingWallBlockItemInterface) blockItem).ballistics$getWallBlock().defaultBlockState().rotate(getDirectionRotation(direction)));
            return true;
        }
        return false;
    }

    private boolean eatIt(LivingEntity player) {
        ItemStack itemStack = getItem();
        if (itemStack.has(DataComponents.CONSUMABLE) || itemStack.has(DataComponents.FOOD)) {
            Consumable consumable = itemStack.getOrDefault(DataComponents.CONSUMABLE,Consumable.builder().build());
            FoodProperties foodProperties = itemStack.getOrDefault(DataComponents.FOOD,new FoodProperties(0,0,false));
            UseRemainder useRemainder = itemStack.get(DataComponents.USE_REMAINDER);
            if (player instanceof Player) foodProperties.onConsume(level(),player,itemStack,consumable);
            else level().playSound(null,player.blockPosition(),SoundEvents.PLAYER_BURP,player.getSoundSource());
            consumable.onConsume(level(),player,itemStack);
            player.hurtServer(((ServerLevel) level()),damageSources().mobProjectile(this, (LivingEntity) getOwner()),1f);
            if (useRemainder != null) spawnItemEntity(useRemainder.convertInto().create());
            return true;
        }
        return false;
    }

    private void attemptEffect(ItemStack itemStack, Item item, MobEffectInstance mobEffectInstance, List<MobEffectInstance> effectList) {
        if (itemStack.is(item)) effectList.add(mobEffectInstance);
    }

    private List<MobEffectInstance> processPotions(ItemStack itemStack) {
        List<MobEffectInstance> effectList = new java.util.ArrayList<>(List.of());
        attemptEffect(itemStack,Items.SLIME_BLOCK,new MobEffectInstance(MobEffects.OOZING,20*20),effectList);
        attemptEffect(itemStack,Items.SLIME_BALL,new MobEffectInstance(MobEffects.OOZING,60*20),effectList);
        attemptEffect(itemStack,Items.WITHER_ROSE,new MobEffectInstance(MobEffects.WITHER,3*20),effectList);
        return effectList;
    }

    protected void onHitEntity(final EntityHitResult hitResult) {
        if (!level().isClientSide()) {
            ItemStack itemStack = getItem();
            Entity entity = hitResult.getEntity();
            if (fireworkExplosion()) return;
            if (placeFluid(hitResult.getEntity().blockPosition(),null)) return;
            if (eatIt(entity.asLivingEntity())) return;
            int damage = 1;
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) Math.clamp(Math.ceil(damage * this.getDeltaMovement().length()),0,Integer.MAX_VALUE));
            if (isTorch()) entity.setRemainingFireTicks(entity.isOnFire() ? entity.getRemainingFireTicks()+60 : 60);
            if (entity instanceof LivingEntity livingEntity) {
                List<MobEffectInstance> effectList = processPotions(itemStack);
                for (MobEffectInstance mobEffectInstance : effectList) {
                    livingEntity.addEffect(mobEffectInstance);
                }
            }
            spawnItemEntity();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (!level().isClientSide()) {
            if (fireworkExplosion()) return;
            if (placeFluid(hitResult.getBlockPos(),hitResult.getDirection())) return;
            if (placeTorch(hitResult.getBlockPos(),hitResult.getDirection())) return;
            spawnItemEntity();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide()) this.kill((ServerLevel) level());
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
