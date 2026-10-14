package llevc.ballistics;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class BallisticsEnchantHelper {
    public static float getDisableBlockAmount(final ItemStack itemStack, final LivingEntity holder, final float die) {
        MutableFloat strength = new MutableFloat(die);
        EnchantmentHelper.runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyUnfilteredValue(ModEnchantmentEffects.disableBlocking,holder.getRandom(), level, strength));
        return strength.floatValue();
    }
}
