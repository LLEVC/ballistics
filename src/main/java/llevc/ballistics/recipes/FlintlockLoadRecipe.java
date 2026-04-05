package llevc.ballistics.recipes;

import llevc.ballistics.ModItems;
import llevc.ballistics.items.FlintlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class FlintlockLoadRecipe extends CustomRecipe {
    public FlintlockLoadRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean potion = false;
        boolean flask = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.Flintlock)) {
                if (!potion) {
                    potion = true;
                } else {
                    return false;
                }
            } else if (itemStack.is(ModItems.Ball)) {
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
        boolean flask = false;
        boolean overflow = false;
        boolean type = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.Flintlock)) {
                if (potion.isEmpty()) {
                    potion = itemStack;
                } else {
                    overflow = true;
                }
            } else if (itemStack.is(ModItems.Ball)) {
                if (!flask) {
                    flask = true;
                } else {
                    overflow = true;
                }
            } else if (!itemStack.isEmpty()) {
                overflow = true;
            }
        }

        if (!potion.isEmpty() && flask && !overflow) {
            ItemStack wsg = potion.copy();
            if (wsg.getItem() instanceof FlintlockItem flintlockItem) {
                if (!flintlockItem.isLoaded(potion)) {
                    flintlockItem.setLoaded(wsg, true);
                    result = wsg;
                }
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<FlintlockLoadRecipe> getSerializer() {
        return FlintlockLoadRecipeSerializer.INSTANCE;
    }
}
