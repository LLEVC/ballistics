package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import llevc.ballistics.Ballistics;
import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SnortableRecipe extends CustomRecipe {
    public SnortableRecipe() {
        super();
    }

    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean potion = false;
        boolean flask = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            Block block = Objects.requireNonNullElse(Block.byItem(itemStack.getItem()), Blocks.AIR);
            if (block != Blocks.AIR || itemStack.is(Items.BRICK)) {
                if (block != Blocks.AIR && !potion) {
                    potion = true;
                } else if (block != Blocks.AIR && potion) {
                    return false;
                }
                if (itemStack.is(Items.BRICK) && !flask) {
                    flask = true;
                } else if (itemStack.is(Items.BRICK) && flask) {
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
        Block block = Blocks.AIR;
        boolean flask = false;
        boolean overflow = false;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack itemStack = recipeInput.getItem(i);
            Block block1 = Objects.requireNonNullElse(Block.byItem(itemStack.getItem()), Blocks.AIR);
            if (block1 != Blocks.AIR) {
                if (block == Blocks.AIR) {
                    block = block1;
                } else {
                    overflow = true;
                }
            } else if (itemStack.is(Items.BRICK)) {

                if (itemStack.is(Items.BRICK) && !flask) {
                    flask = true;
                } else if (itemStack.is(Items.BRICK) && flask) {
                    overflow = true;
                }
            } else if (!itemStack.isEmpty()) {
                overflow = true;
            }
        }

        if (!overflow && flask && block != Blocks.AIR) {
            ItemStack yes = ModItems.createSnortable(block);
            result = yes;
        }

        return result;
    }

    public static final SnortableRecipe instance = new SnortableRecipe();
    public static final MapCodec<SnortableRecipe> MAP_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, SnortableRecipe> STREAM_CODEC;
    public static final RecipeSerializer<SnortableRecipe> SERIALIZER;

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
