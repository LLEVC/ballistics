package llevc.ballistics.recipes;

import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class GeekedRecipe extends CustomRecipe {
    public GeekedRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean potion = false;
        boolean flask = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.cartridge)) {
                if (!potion) {
                    potion = true;
                } else {
                    return false;
                }
            } else if (itemStack.is(ModItems.vape)) {
                if (!flask) {
                    flask = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return (potion && flask && recipeInput.ingredientCount() <= 2);
    }

    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;
        ItemStack geek = ItemStack.EMPTY;
        boolean overflow = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.cartridge)) {

                if (potion.isEmpty()) {
                    potion = itemStack;
                } else {
                    overflow = true;
                }

            } else if (itemStack.is(ModItems.vape)) {

                if (geek.isEmpty()) {
                    geek = itemStack;
                } else {
                    overflow = true;
                }

            } else if (!itemStack.isEmpty()) {
                overflow = true;
            }
        }

        if (!potion.isEmpty() && !geek.isEmpty() && !overflow) {
            PotionContents death = Objects.requireNonNull(potion.get(DataComponents.POTION_CONTENTS));
            double level = 0;
            List<String> effects = new java.util.ArrayList<>(List.of());
            for (MobEffectInstance mobEffectInstance : death.getAllEffects()) {
                level = level+mobEffectInstance.getAmplifier()+1;
                if (!effects.contains(mobEffectInstance.getEffect().getRegisteredName())) {
                    effects.add(mobEffectInstance.getEffect().getRegisteredName());
                }
            }
            //Ballistics.LOGGER.info(String.valueOf(death));
            ItemStack gamer;
            gamer = geek.copy();
            gamer.set(DataComponents.POTION_CONTENTS, death);
            result = gamer;
        }

        return result;
    }

    @Override
    public RecipeSerializer<GeekedRecipe> getSerializer() {
        return GeekedRecipeSerializer.INSTANCE;
    }
}
