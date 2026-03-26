package llevc.ballistics;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class FlaskRecipe extends CustomRecipe {
    public FlaskRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean potion = false;
        boolean flask = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.EmptyFlask) || itemStack.is(Items.POTION)) {
                if (itemStack.is(Items.POTION) && !potion) {
                    potion = true;
                } else if (itemStack.is(Items.POTION) && potion) {
                    return false;
                }
                if (itemStack.is(ModItems.EmptyFlask) && !flask) {
                    flask = true;
                } else if (itemStack.is(ModItems.EmptyFlask) && flask) {
                    return false;
                }
            } else if (itemStack.is(ModItems.Flask) || itemStack.is(Items.GLASS_BOTTLE)) {
                if (itemStack.is(ModItems.Flask) && itemStack.getOrDefault(DataComponents.RARITY,Rarity.COMMON).equals(Rarity.COMMON) && !potion) {
                    potion = true;
                } else if (itemStack.is(ModItems.Flask) && potion) {
                    return false;
                }
                if (itemStack.is(Items.GLASS_BOTTLE) && !flask) {
                    flask = true;
                } else if (itemStack.is(Items.GLASS_BOTTLE) && flask) {
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
        boolean flask = false;
        boolean overflow = false;
        boolean type = false;
        boolean typeSet = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if ((itemStack.is(ModItems.EmptyFlask) || itemStack.is(Items.POTION)) && (!type || !typeSet)) {
                typeSet = true;
                type = false;
                if (itemStack.is(Items.POTION) && potion.isEmpty()) {
                    potion = itemStack;
                } else if (itemStack.is(Items.POTION) && !potion.isEmpty()) {
                    overflow = true;
                }
                if (itemStack.is(ModItems.EmptyFlask) && !flask) {
                    flask = true;
                } else if (itemStack.is(ModItems.EmptyFlask) && flask) {
                    overflow = true;
                }
            } else if ((itemStack.is(Items.GLASS_BOTTLE) || itemStack.is(ModItems.Flask)) && (type || !typeSet)) {
                typeSet = true;
                type = true;
                if (itemStack.is(ModItems.Flask) && potion.isEmpty()) {
                    potion = itemStack;
                } else if (itemStack.is(ModItems.Flask) && !potion.isEmpty()) {
                    overflow = true;
                }
                if (itemStack.is(Items.GLASS_BOTTLE) && !flask) {
                    flask = true;
                } else if (itemStack.is(Items.GLASS_BOTTLE) && flask) {
                    overflow = true;
                }
            } else if (!itemStack.isEmpty()) {
                overflow = true;
            }
        }

        if (!potion.isEmpty() && flask && !overflow) {
            Holder<Potion> death = Objects.requireNonNull(potion.get(DataComponents.POTION_CONTENTS)).potion().orElse(Potions.WATER);
            result = PotionContents.createItemStack(ModItems.Flask, death);
        }

        return result;
    }

    @Override
    public RecipeSerializer<FlaskRecipe> getSerializer() {
        return FlaskRecipeSerializer.INSTANCE;
    }
}
