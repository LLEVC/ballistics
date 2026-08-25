package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

public class CartridgeRecipe extends CustomRecipe {
    public CartridgeRecipe() {
        super();
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean potion = false;
        boolean flask = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(Items.POTION)) {
                if (!potion && itemStack.getRarity() == Rarity.COMMON) {
                    potion = true;
                } else {
                    return false;
                }
            } else if (itemStack.is(ModItems.cartridge)) {
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

    public ItemStack assemble(CraftingInput recipeInput) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;
        boolean flask = false;
        boolean overflow = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (itemStack.is(Items.POTION)) {

                if (potion.isEmpty()) {
                    potion = itemStack;
                } else {
                    overflow = true;
                }

            } else if (itemStack.is(ModItems.cartridge)) {

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
            gamer = PotionContents.createItemStack(ModItems.cartridge, death.potion().orElse(Potions.WATER));
            gamer.set(DataComponents.POTION_CONTENTS, death);
            result = gamer;
        }

        return result;
    }

    public static final CartridgeRecipe instance = new CartridgeRecipe();
    public static final MapCodec<CartridgeRecipe> MAP_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, CartridgeRecipe> STREAM_CODEC;
    public static final RecipeSerializer<CartridgeRecipe> SERIALIZER;

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
