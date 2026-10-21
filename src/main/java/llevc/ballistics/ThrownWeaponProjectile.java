package llevc.ballistics;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

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
