package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import llevc.ballistics.Ballistics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionItem.class)
public class PotionMixin extends Item {
    public PotionMixin(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        //Ballistics.LOGGER.info(String.valueOf(super.getRecipeRemainder(stack)));
        if (super.getRecipeRemainder(stack).isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        return super.getRecipeRemainder(stack);
    }
}
