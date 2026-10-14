package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.BallisticsEnchantHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getActiveItem();

    @Shadow
    public abstract @Nullable LivingEntity asLivingEntity();

    @Shadow
    public abstract ItemStack getWeaponItem();

    @WrapMethod(method = "getSecondsToDisableBlocking")
    public float bro(Operation<Float> original) {
        float value = original.call();
        return BallisticsEnchantHelper.getDisableBlockAmount(this.getWeaponItem(), this.asLivingEntity(),value);
    }
}
