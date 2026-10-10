package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.PlayerInterface;
import llevc.ballistics.items.FireworkLauncherItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerInterface {
    @Shadow public abstract boolean hasInfiniteMaterials();

    @Unique
    public int ballistics$startThrowChargeTime = 0;
    @Unique
    public boolean ballistics$useThrowCharge = false;

    public void ballistics$startThrowCharge(int time) {
        ballistics$startThrowChargeTime = time;
        ballistics$useThrowCharge = true;
    }
    public void ballistics$resetThrowCharge() {
        ballistics$startThrowChargeTime = 0;
        ballistics$useThrowCharge = false;
    }
    public int ballistics$getThrowChargeTime() {
        return ballistics$startThrowChargeTime;
    }
    public boolean ballistics$getUseThrowCharge() {
        return ballistics$useThrowCharge;
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
