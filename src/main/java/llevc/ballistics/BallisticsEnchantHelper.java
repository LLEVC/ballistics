package llevc.ballistics;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.apache.commons.lang3.mutable.MutableFloat;

public class BallisticsEnchantHelper {
    public static float getEnchantValue(final ItemStack itemStack, final LivingEntity holder, final float die, DataComponentType<EnchantmentValueEffect> eve) {
        MutableFloat strength = new MutableFloat(die);
        EnchantmentHelper.runIterationOnItem(itemStack, (enchantment, level) -> ((Enchantment)enchantment.value()).modifyUnfilteredValue(eve,holder.getRandom(), level, strength));
        return strength.floatValue();
    }

    public static float getDisableBlockAmount(final ItemStack itemStack, final LivingEntity holder, final float die) {
        return getEnchantValue(itemStack,holder,die,ModEnchantmentEffects.disableBlocking);
    }

    public static float getThrowCooldown(final ItemStack itemStack, final LivingEntity holder, final float die) {
        return getEnchantValue(itemStack,holder,die,ModEnchantmentEffects.throwCooldown);
    }

    public static float getThrowPower(final ItemStack itemStack, final LivingEntity holder, final float die) {
        return getEnchantValue(itemStack,holder,die,ModEnchantmentEffects.throwPower);
    }

    public static float getOverchargeAmount(final ItemStack itemStack, final LivingEntity holder, final float die) {
        return getEnchantValue(itemStack,holder,die,ModEnchantmentEffects.overchargeThrows);
    }
}
