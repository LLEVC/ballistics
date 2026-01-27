package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.Ballistics;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkMixin extends Projectile implements ItemSupplier {
	@Shadow protected abstract List<FireworkExplosion> getExplosions();

	public FireworkMixin(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
	}

	//@Inject(method = "dealExplosionDamage", at = @At("HEAD"))
	@Unique
	public void jump(ServerLevel world, Entity owner) {
		List<FireworkExplosion> freaky = this.getExplosions();
		if (owner != null) {
			Vec3 direction = (owner.position().subtract(this.position()));
			if (direction.length() < 5) {
				double ayo = (4+((double) freaky.size() /2))/direction.length();
				Vec3 yo = direction.multiply(ayo,ayo,ayo);
				Ballistics.LOGGER.info(yo.toString());
				owner.resetFallDistance();
				owner.setDeltaMovement(yo);
			}
		}
	}

	@WrapMethod(method="dealExplosionDamage")
	public void init(ServerLevel serverLevel, Operation<Void> original) {
		if ((Object) this instanceof FireworkRocketEntity) {
			jump(serverLevel,this.getOwner());
		} else {
			original.call(serverLevel);
		}
	}
}