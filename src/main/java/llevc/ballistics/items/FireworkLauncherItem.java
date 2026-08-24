package llevc.ballistics.items;

import com.ibm.icu.util.EthiopicCalendar;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class FireworkLauncherItem extends CrossbowItem {
    public FireworkLauncherItem(Properties properties) {
        super(properties);
    }

    public static final Predicate<ItemStack> FireworkOnly = itemStack -> itemStack.is(Items.FIREWORK_ROCKET);

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles() {
        return FireworkOnly;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return FireworkOnly;
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl) {
        return new FireworkRocketEntity(level, itemStack2, livingEntity, livingEntity.getX(), livingEntity.getEyeY() - 0.15F, livingEntity.getZ(), true);
    }
}
