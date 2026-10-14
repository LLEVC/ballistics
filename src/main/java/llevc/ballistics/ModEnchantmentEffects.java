package llevc.ballistics;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

import java.util.function.UnaryOperator;

public class ModEnchantmentEffects {
    public static final DataComponentType<EnchantmentValueEffect> disableBlocking = register("disable_blocking",(b) -> b.persistent(EnchantmentValueEffect.CODEC));
    public static final DataComponentType<Unit> quickload = register("quickload",(b) -> b.persistent(Unit.CODEC));
    public static final DataComponentType<Unit> flintlockRaycast = register("flintlock_raycast",(b) -> b.persistent(Unit.CODEC));

    public static <T> DataComponentType<T> register(final String id, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return (DataComponentType) Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,id), ((DataComponentType.Builder)builder.apply(DataComponentType.builder())).build());
    }

    public static void init() {}
}
