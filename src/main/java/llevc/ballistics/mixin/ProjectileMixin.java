package llevc.ballistics.mixin;

import llevc.ballistics.ProjectileInterface;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements ProjectileInterface {
    @Shadow
    private boolean leftOwnerChecked;

    @Shadow
    private boolean hasBeenShot;

    @Shadow
    public abstract @Nullable Entity getOwner();

    public ProjectileMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void ballistics$setLeftOwnerChecked(boolean bool) {
        this.leftOwnerChecked = bool;
    }

    @Override
    public void ballistics$doShootCheck() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }
    }

    @Unique protected void defineSynchedData(SynchedEntityData.Builder entityData) {}
    @Unique public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {return false;}
    @Unique protected void readAdditionalSaveData(ValueInput input) {}
    @Unique protected void addAdditionalSaveData(ValueOutput output) {}
}
