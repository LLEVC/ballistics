package llevc.ballistics.recipes;

import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class MixedDrinkRecipe extends CustomRecipe {
    public MixedDrinkRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean mixer = false;
        int potions = 0;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.EmptyFlask)) {
                potions++;
            } else if (itemStack.is(ModItems.DrinkMixer)) {
                if (!mixer && itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY) != PotionContents.EMPTY) {
                    mixer = true;
                } else {
                    return false;
                }
            } else if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return (mixer && 0 < potions && potions <= 4);
    }

    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
        ItemStack result = ModItems.Flask.getDefaultInstance();
        ItemStack mixer = ItemStack.EMPTY;
        int flasks = 0;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.EmptyFlask)) {
                flasks++;
            } else if (itemStack.is(ModItems.DrinkMixer)) {
                if (mixer.isEmpty() && itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY) != PotionContents.EMPTY) {
                    mixer = itemStack;
                } else {
                    return ItemStack.EMPTY;
                }
            } else if (!itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        Iterable<MobEffectInstance> what = mixer.get(DataComponents.POTION_CONTENTS).getAllEffects();
        List<MobEffectInstance> bbl = new java.util.ArrayList<>(List.of());
        double level = 0;
        for (MobEffectInstance mobEffectInstance : what) {
            MobEffectInstance newEffect = mobEffectInstance.withScaledDuration((float) 1 / flasks);
            bbl.add(newEffect);
            level = level+1+mobEffectInstance.getAmplifier();
        }

        PotionContents huh = new PotionContents(Optional.of(BuiltInRegistries.POTION.wrapAsHolder(ModItems.MixedPotion)),Optional.empty(),bbl,Optional.empty());
        ItemStack wowwie = new ItemStack(ModItems.Flask);
        wowwie.set(DataComponents.POTION_CONTENTS,huh);
        wowwie.setCount(flasks);
        if (level > 2*(2+bbl.size())) {
            wowwie.set(DataComponents.RARITY, Rarity.RARE);
        }
        result = wowwie;

        return result;
    }

    @Override
    public RecipeSerializer<MixedDrinkRecipe> getSerializer() {
        return MixedDrinkRecipeSerializer.INSTANCE;
    }
}
