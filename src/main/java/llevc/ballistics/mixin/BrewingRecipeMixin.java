package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(BrewingRecipe.class)
public class BrewingRecipeMixin {
    @WrapMethod(method = "assemble(Lnet/minecraft/world/item/crafting/BrewingInput;)Lnet/minecraft/world/item/ItemStack;")
    public ItemStack vro(BrewingInput input, Operation<ItemStack> original) {
        ItemStack value = original.call(input);
        ItemStack newValue = value.copy();
        Optional<Holder<Potion>> yea = input.input().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
        if (yea.isPresent() && yea.get().value() == ModItems.MixedPotion && (value.is(Items.SPLASH_POTION) || value.is(Items.LINGERING_POTION))) {
            newValue.set(DataComponents.POTION_CONTENTS,input.input().getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY));
            newValue.set(DataComponents.RARITY,input.input().getRarity());
        }
        return newValue;
    }
}
