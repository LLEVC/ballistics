package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import llevc.ballistics.Ballistics;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionItem.class)
public class PotionMixin extends Item {
    public PotionMixin(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        //Ballistics.LOGGER.info(String.valueOf(super.getRecipeRemainder(stack)));
        if (super.getCraftingRemainder(stack) == null) {
            return new ItemStackTemplate(Items.GLASS_BOTTLE);
        }
        return super.getCraftingRemainder(stack);
    }
}
