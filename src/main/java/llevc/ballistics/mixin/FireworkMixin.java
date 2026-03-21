package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.Ballistics;
import llevc.ballistics.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.OptionalInt;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkMixin extends Projectile implements ItemSupplier {

	@Shadow protected abstract List<FireworkExplosion> getExplosions();

	@Shadow public abstract boolean isShotAtAngle();

	@Shadow private @Nullable LivingEntity attachedToEntity;

	@Shadow protected abstract boolean isAttachedToEntity();

	@Shadow @Final private static EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET;

	@Shadow protected abstract void explode(ServerLevel serverLevel);

	public FireworkMixin(EntityType<? extends Projectile> entityType, Level world, boolean attachedToEntity) {
		super(entityType, world);
    }

	//@Inject(method = "dealExplosionDamage", at = @At("HEAD"))
	@Unique
	public void jump(ServerLevel world, Entity owner) {
		List<FireworkExplosion> freaky = this.getExplosions();
		if (owner != null) {
			Vec3 direction = (owner.position().subtract(this.position()));
			float sh = 5.0f + freaky.size() * 2.0f;
			if (direction.length() < sh/2.0f) {
				double ayo = Math.clamp(Math.abs((sh / (direction.length()*10) + (1.5 * freaky.size())) * freaky.size()),0, Math.round(10 * Math.sqrt(freaky.size()))/2.0)/3.5;
				Vec3 yo = direction.normalize().multiply(ayo,ayo,ayo);
				Ballistics.LOGGER.info(yo.toString());
				double damag = 1 + (freaky.size()*freaky.size());
				//Ballistics.LOGGER.info(String.valueOf(damag));
				owner.hurtServer(world,damageSources().fireworks((FireworkRocketEntity) world.getEntity(this.uuid),owner), (float) damag);
				owner.resetFallDistance();
				owner.setDeltaMovement(yo);
			}
		}
	}

	@Unique
	public void fireworkHit(ServerLevel serverLevel, Operation<Void> original) {
		if (!this.getExplosions().isEmpty()) {
			if (this.isShotAtAngle() ) {
				jump(serverLevel, this.getOwner());
			}
			for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0))) {
				if (livingEntity.isAlive()) {
					List<FireworkExplosion> freaky = this.getExplosions();
					Vec3 direction = (livingEntity.position().subtract(this.position()));
					ClipContext heyosssa = new ClipContext(this.position().add(0, this.getBoundingBox().getYsize(), 0), (livingEntity.position().add(0, livingEntity.getBoundingBox().getYsize() / 2, 0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, serverLevel.getEntity(this.uuid));
					HitResult hitResult = serverLevel.clip(heyosssa);
					float sh = 5.0f + freaky.size() * 2.0f;
					boolean hit = false;
					if (hitResult.getType() == HitResult.Type.MISS) {
						hit = true;
					}
					if (hit && (!livingEntity.is(getOwner()) || !this.isShotAtAngle())) {
						float damag = Math.round(sh * Math.sqrt(((5 * freaky.size()) - (direction.length() + ((3.5 * freaky.size()) - 3.5))) / (freaky.size() * (3 - ((freaky.size() - 1.0) / 6)))));
						livingEntity.hurtServer(serverLevel, damageSources().fireworks((FireworkRocketEntity) serverLevel.getEntity(this.uuid), getOwner()), damag);
						Ballistics.LOGGER.info(String.valueOf(damag));
					}
				}
			}
		}
		//original.call(serverLevel);
	}

	@WrapMethod(method="dealExplosionDamage")
	public void init(ServerLevel serverLevel, Operation<Void> original) {
		if ((Object) this instanceof FireworkRocketEntity) {
			fireworkHit(serverLevel,original);
		} else {
			original.call(serverLevel);
		}
	}

	@Unique
	private int themticks = 5;
	private boolean heyoo = false;

	@WrapMethod(method = "tick")
	public void init(Operation<Void> original) {
		if (this.isAttachedToEntity()) {
			if (this.attachedToEntity != null && themticks > 0) {
				Vec3 vec33 = Vec3.ZERO;
				if (this.attachedToEntity.isVisuallyCrawling() || this.attachedToEntity.isHolding(ModItems.rhGlove)) {
					Vec3 vec3 = this.attachedToEntity.getLookAngle();
					Vec3 vec32 = this.attachedToEntity.getDeltaMovement();
					this.attachedToEntity
							.setDeltaMovement(
									vec32.add(vec3.x * 0.1 + (vec3.x * 1.5 - vec32.x) * 0.5, vec3.y * 0.1 + (vec3.y * 1.5 - vec32.y) * 0.5, vec3.z * 0.1 + (vec3.z * 1.5 - vec32.z) * 0.5)
							);
					vec33 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);

				}

				this.setPos(this.attachedToEntity.getX() + vec33.x, this.attachedToEntity.getY() + vec33.y, this.attachedToEntity.getZ() + vec33.z);
				this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());

				themticks--;
			}
		}
		original.call();
	}
}