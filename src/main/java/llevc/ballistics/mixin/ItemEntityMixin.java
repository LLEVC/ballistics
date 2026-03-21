package llevc.ballistics.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract @Nullable Entity getOwner();

    private float airTime = 0f;
    private boolean primed = false;
    private boolean exploded = false;
    private int amount = 1;

    @Inject(method = "tick",at=@At("TAIL"))
    public void init(CallbackInfo ci) {

        if (!this.onGround()) {
            airTime = airTime + 1f;
        }
        if (this.getDeltaMovement().length() > 1.5) {
            primed = true;
        }
        if (!exploded) {
            amount = this.getItem().getCount();
        }
        if (this.getItem().is(Items.END_CRYSTAL)) {
            if (airTime > 10f && primed && this.onGround()) {
                DamageSource damageSource2 = this.getOwner() != null ? this.damageSources().explosion(this, this.getOwner()) : null;
                this.level().explode(this, damageSource2, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                exploded = true;
                amount = amount-1;
                if (amount < 1) {
                    this.discard();
                }
            }
        }
    }
}
