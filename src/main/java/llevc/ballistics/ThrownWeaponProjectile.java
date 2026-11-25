package llevc.ballistics;

import net.fabricmc.fabric.impl.item.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ThrownWeaponProjectile extends AbstractArrow {
    private static final EntityDataAccessor<ItemStack> pickupItemStackData;

    public final float bobOffs;
    public final boolean directional;
    
    protected ThrownWeaponProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
        this.directional = true;
        this.entityData.set(pickupItemStackData,getDefaultPickupItem());
    }

    protected ThrownWeaponProjectile(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level level, ItemStack pickupItemStack) {
        super(type, x, y, z, level, pickupItemStack, pickupItemStack);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
        this.directional = true;
        this.entityData.set(pickupItemStackData,pickupItemStack);
    }

    protected ThrownWeaponProjectile(LivingEntity mob, Level level, ItemStack pickupItemStack) {
        super(ModEntities.thrownWeaponProjectile, mob, level, pickupItemStack, pickupItemStack);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
        this.directional = true;
        this.entityData.set(pickupItemStackData,pickupItemStack);
    }

    public ThrownWeaponProjectile(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        this(livingEntity,serverLevel,itemStack);
    }

    public Vec3 getModelRotationOffset() {
        return new Vec3(0, 0, 0);
    }

    public Vec3 getModelPositionOffset() {
        return new Vec3(0, 0, 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (level() instanceof ServerLevel serverLevel) {
            DamageSource damageSource = getOwner() == null ? damageSources().generic() : damageSources().playerAttack((Player) getOwner());
            ItemAttributeModifiers itemAttributeModifiers = getItem().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,ItemAttributeModifiers.EMPTY);
            float damage = (float) Math.round(itemAttributeModifiers.compute(Attributes.ATTACK_DAMAGE,1f,EquipmentSlot.MAINHAND) * Math.clamp(this.getDeltaMovement().length()/(getItem().has(DataComponents.KINETIC_WEAPON) ? 1:2),1f,Float.MAX_VALUE));
            damage = EnchantmentHelper.modifyDamage(serverLevel, getItem(), entity, damageSource, damage);
            damage += getItem().getItem().getAttackDamageBonus(entity,damage,damageSource);

            boolean hit = entity.hurtServer(serverLevel,damageSource,damage);
            Ballistics.LOGGER.info(String.valueOf(damage));

            if (hit) {
                EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(serverLevel, entity, damageSource, this.getItem(), (weapon) -> this.kill(serverLevel));

                if (entity instanceof LivingEntity livingEntity) {
                    this.doKnockback(livingEntity, damageSource);
                    this.doPostHurtEffects(livingEntity);
                    getItem().getItem().postHurtEnemy(getItem(),livingEntity, Objects.requireNonNull(getOwner().asLivingEntity()));
                }
            }

            if (entity.projectileReceivesSideEffectsOnHit(hit)) this.deflect(ProjectileDeflection.REVERSE, entity, this.owner, false, new Vec3(0.02, 0.2, 0.02));
        }

        //super.onHitEntity(hitResult);
    }

    public boolean fuc = true;
    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);

        if (this.getItem().has(DataComponents.BLOCK_TRANSFORMER)) {
            BlockTransformer blockTransformer = Objects.requireNonNull(this.getItem().get(DataComponents.BLOCK_TRANSFORMER)).value();
            for (int a = 0; a < 3; a++) {
                for (int b = 0; b < 3; b++) {
                    for (int c = 0; c < 3; c++) {
                        BlockPos off = blockPosition().offset(1-a,1-b,1-c);
                        //Ballistics.LOGGER.info(String.valueOf(off));
                        BlockState blockState = level().getBlockState(off);
                        for (BlockTransformer.BlockTransformData blockTransformData : blockTransformer.transforms()) {
                            BlockState newBlockState = blockTransformData.blockStateProvider().value().getOptionalState(level(),level().getRandom(),off);
                            if (newBlockState != null) {
                                level().setBlockAndUpdate(off,newBlockState);
                                level().playSound(null, off, blockTransformData.sound().value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                blockTransformData.particle().send(level(), getOwner(), off);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (this.getItem().has(DataComponents.TOOL) && ((fuc || !hitResult.getDirection().equals(Direction.UP)))) {
            fuc = false;
            Ballistics.LOGGER.info(String.valueOf(hitResult.getDirection()));
            Tool tool = this.getItem().get(DataComponents.TOOL);
            for (int a = 0; a < 3; a++) {
                for (int b = 0; b < 3; b++) {
                    for (int c = 0; c < 3; c++) {
                        BlockPos off = blockPosition().offset(1-a,1-b,1-c);
                        BlockState blockState = level().getBlockState(off);
                        if (tool.isCorrectForDrops(blockState)) {
                            Block.dropResources(blockState,level(),off,level().getBlockEntity(off),this,getItem());
                            level().destroyBlock(off,false,this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void setPickupItemStack(ItemStack itemStack) {
        super.setPickupItemStack(itemStack);
        setItem(itemStack);
    }

    public void setItem(final ItemStack source) {
        this.entityData.set(pickupItemStackData, source.copyWithCount(1));
    }

    public ItemStack getItem() {
        return (ItemStack) this.entityData.get(pickupItemStackData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(pickupItemStackData, this.getDefaultPickupItem().copyWithCount(1));
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.WOODEN_SPEAR);
    }

    static {
        pickupItemStackData = SynchedEntityData.defineId(ThrownWeaponProjectile.class, EntityDataSerializers.ITEM_STACK);
    }
}
