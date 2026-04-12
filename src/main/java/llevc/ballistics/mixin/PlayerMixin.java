package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.items.FireworkLauncherItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow public abstract boolean hasInfiniteMaterials();

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
