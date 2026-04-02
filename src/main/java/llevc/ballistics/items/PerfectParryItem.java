package llevc.ballistics.items;

import llevc.ballistics.Ballistics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class PerfectParryItem extends ShieldItem {
    public PerfectParryItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        int bbl = getUseDuration(itemStack,livingEntity)-i;
        if (bbl < 20) {
            //Ballistics.LOGGER.info(String.valueOf(bbl));
            Objects.requireNonNull(itemStack.get(DataComponents.BLOCKS_ATTACKS));
        }
    }
}
