package llevc.ballistics.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @Inject(method = "mix",at = @At("RETURN"), cancellable = true)
    public void init(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack what = cir.getReturnValue();
        if (what.is(Items.SPLASH_POTION) || what.is(Items.LINGERING_POTION)) {
            what.set(DataComponents.POTION_CONTENTS, itemStack2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
            cir.setReturnValue(what);
        }
    }
}
