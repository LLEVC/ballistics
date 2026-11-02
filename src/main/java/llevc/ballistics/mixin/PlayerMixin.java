package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.Ballistics;
import llevc.ballistics.PlayerInterface;
import llevc.ballistics.items.FireworkLauncherItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerInterface {
    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow public abstract boolean hasInfiniteMaterials();

    @Shadow
    public abstract ItemStack getWeaponItem();

    @Unique
    public int ballistics$startThrowChargeTime = 0;
    @Unique
    public boolean ballistics$useThrowCharge = false;
    @Unique
    public int ballistics$maxThrowCharge = 40;
    @Unique
    public int ballistics$maxOvercharge = 0;

    public void ballistics$startThrowCharge(int time) {
        ballistics$startThrowChargeTime = time;
        ballistics$maxThrowCharge = Ballistics.getMaxChargeTime(this.getMainHandItem(),this.asLivingEntity());
        ballistics$maxOvercharge = Ballistics.getMaxOvercharge(this.getMainHandItem(),this.asLivingEntity());
        ballistics$useThrowCharge = true;
    }
    public void ballistics$resetThrowCharge() {
        ballistics$startThrowChargeTime = 0;
        ballistics$maxThrowCharge = 40;
        ballistics$maxOvercharge = 0;
        ballistics$useThrowCharge = false;
    }
    public int ballistics$getThrowChargeTime() {
        return ballistics$startThrowChargeTime;
    }
    public boolean ballistics$getUseThrowCharge() {
        return ballistics$useThrowCharge;
    }
    public int ballistics$getMaxChargeTime() {
        return ballistics$maxThrowCharge;
    }
    public int ballistics$getMaxOvercharge() {
        return ballistics$maxOvercharge;
    }

    @WrapMethod(method = "getProjectile")
    public ItemStack init(ItemStack itemStack, Operation<ItemStack> original) {
        ItemStack ogItem = original.call(itemStack);
        ItemStack result = ogItem;

        if (this.hasInfiniteMaterials() && ogItem.is(Items.ARROW) && itemStack.getItem() instanceof FireworkLauncherItem) {
            result = Items.FIREWORK_ROCKET.getDefaultInstance();
        }

        return result;
    }
}
