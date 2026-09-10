package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class FlaskRecipe extends CustomRecipe {
    public FlaskRecipe() {
        super();
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
                if (itemStack.is(ModItems.Flask) && !potion) {
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

    public ItemStack assemble(CraftingInput recipeInput) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;
        boolean flask = false;
        boolean overflow = false;
        boolean type = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(ModItems.Flask) || itemStack.is(Items.POTION)) {

                if ((itemStack.is(Items.POTION) || itemStack.is(ModItems.Flask)) && potion.isEmpty()) {
                    potion = itemStack;
                } else if (itemStack.is(Items.POTION) && !potion.isEmpty()) {
                    overflow = true;
                }

            } else if (itemStack.is(Items.GLASS_BOTTLE) || itemStack.is(ModItems.EmptyFlask)) {

                if (itemStack.is(ModItems.EmptyFlask) && !flask) {
                    flask = true;
                } else if (itemStack.is(Items.GLASS_BOTTLE) && !flask) {
                    type = true;
                    flask = true;
                } else if ((itemStack.is(Items.GLASS_BOTTLE) || itemStack.is(ModItems.EmptyFlask)) && flask) {
                    overflow = true;
                }
            } else if (!itemStack.isEmpty()) {
                overflow = true;
            }
        }

        if (!potion.isEmpty() && flask && !overflow) {
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
            if (type) {
                gamer = PotionContents.createItemStack(Items.POTION, death.potion().orElse(Potions.WATER));
            } else {
                gamer = PotionContents.createItemStack(ModItems.Flask, death.potion().orElse(Potions.WATER));
            }
            gamer.set(DataComponents.POTION_CONTENTS, death);
            if (level > 2*(effects.size())) {
                gamer.set(DataComponents.RARITY,Rarity.RARE);
            }
            result = gamer;
        }

        return result;
    }

    public static final FlaskRecipe instance = new FlaskRecipe();
    public static final MapCodec<FlaskRecipe> MAP_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskRecipe> STREAM_CODEC;
    public static final RecipeSerializer<FlaskRecipe> SERIALIZER;

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }

    static {
        MAP_CODEC = MapCodec.unit(instance);
        STREAM_CODEC = StreamCodec.unit(instance);
        SERIALIZER = new RecipeSerializer(MAP_CODEC, STREAM_CODEC);
    }
}
