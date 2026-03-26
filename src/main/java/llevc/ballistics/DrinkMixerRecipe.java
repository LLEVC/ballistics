package llevc.ballistics;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.CommonColors;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DrinkMixerRecipe extends CustomRecipe {
    public DrinkMixerRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean mixer = false;
        int potions = 0;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(Items.POTION)) {
                potions++;
            } else if (itemStack.is(ModItems.DrinkMixer)) {
                if (!mixer && itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY) == PotionContents.EMPTY) {
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
        ItemStack result = ModItems.DrinkMixer.getDefaultInstance();
        boolean mixer = false;
        List<PotionContents> mhm = new java.util.ArrayList<>(List.of());

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(Items.POTION)) {
                PotionContents heyo = itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
                mhm.add(heyo);
            } else if (itemStack.is(ModItems.DrinkMixer)) {
                if (!mixer && itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY) == PotionContents.EMPTY) {
                    mixer = true;
                } else {
                    return ItemStack.EMPTY;
                }
            } else if (!itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        List<MobEffectInstance> effects = new java.util.ArrayList<>(List.of());
        List<String> effectsString = new java.util.ArrayList<>(List.of());
        for (PotionContents potionContents : mhm) {
            for (MobEffectInstance sup : potionContents.getAllEffects()) {
                if (effectsString.contains(sup.getEffect().getRegisteredName())) {
                    int mindex = effectsString.indexOf(sup.getEffect().getRegisteredName());
                    MobEffectInstance lmao = effects.get(mindex);
                    MobEffectInstance newOne = new MobEffectInstance(
                            sup.getEffect(),
                            sup.getDuration()+lmao.getDuration(),
                            sup.getAmplifier()+lmao.getAmplifier(),
                            (!sup.isAmbient() || !lmao.isAmbient()),
                            (sup.isVisible() || lmao.isVisible()),
                            (sup.showIcon() || lmao.showIcon())
                    );
                    effects.set(mindex,newOne);
                } else {
                    effects.add(sup);
                    effectsString.add(sup.getEffect().getRegisteredName());
                }
            }
        }

        PotionContents what = new PotionContents(Optional.empty(),Optional.empty(),effects,Optional.empty());
        ItemStack wowwie = new ItemStack(ModItems.DrinkMixer);
        wowwie.set(DataComponents.POTION_CONTENTS,what);
        result = wowwie;

        return result;
    }

    @Override
    public RecipeSerializer<DrinkMixerRecipe> getSerializer() {
        return DrinkMixerRecipeSerializer.INSTANCE;
    }
}
