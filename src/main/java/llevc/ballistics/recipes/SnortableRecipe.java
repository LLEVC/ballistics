package llevc.ballistics.recipes;

import llevc.ballistics.Ballistics;
import llevc.ballistics.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
    public SnortableRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
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

    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
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
            ItemStack yes = new ItemStack(ModItems.snortable);
            List<MobEffectInstance> effectInstanceList = new ArrayList<>();

            if (block.getFriction() > 0.75) { // slippppery
                effectInstanceList.add(new MobEffectInstance(MobEffects.SPEED, (int) Math.ceil(400/block.getFriction())));
            }
            if (block.getSpeedFactor() < 1) { // is it sticky?
                effectInstanceList.add(new MobEffectInstance(MobEffects.SLOWNESS, (int) Math.ceil(200/block.getSpeedFactor())));
            }
            if (block.getJumpFactor() < 1) { // sticky 2, only honey has this one
                effectInstanceList.add(new MobEffectInstance(MobEffects.SLOW_FALLING,(int) Math.ceil(200/block.getJumpFactor())));
            } else if (block.getJumpFactor() > 1) { // bouncy but not bouncy
                effectInstanceList.add(new MobEffectInstance(MobEffects.JUMP_BOOST,(int) Math.ceil(200*block.getJumpFactor())));
            }
            if (block instanceof InfestedBlock) { // is infested?
                effectInstanceList.add(new MobEffectInstance(MobEffects.INFESTED,60*20));
            } else if (block instanceof SlimeBlock) { // slimme
                effectInstanceList.add(new MobEffectInstance(MobEffects.OOZING,600));
            } else if (block instanceof TntBlock) {
                effectInstanceList.add(new MobEffectInstance(ModItems.ExplodePotionEffect));
            } else if (block instanceof WebBlock) { // cobweb
                effectInstanceList.add(new MobEffectInstance(MobEffects.WEAVING,1200));
            }
            if (block.defaultDestroyTime() > 20 || block.defaultDestroyTime() < 0) { // long/impossible to break
                effectInstanceList.add(new MobEffectInstance(MobEffects.MINING_FATIGUE, 1000));
                effectInstanceList.add(new MobEffectInstance(MobEffects.RESISTANCE,1000));
            } else if (block.defaultDestroyTime() < 0.5 && block.defaultDestroyTime() >= 0) { // instant break
                effectInstanceList.add(new MobEffectInstance(MobEffects.HASTE, (int) Math.ceil(200/(0.5+block.defaultDestroyTime()))));
            }

            PotionContents huh = new PotionContents(Optional.of(BuiltInRegistries.POTION.wrapAsHolder(ModItems.MixedPotion)),Optional.empty(),effectInstanceList,Optional.empty());
            yes.set(DataComponents.POTION_CONTENTS,huh);
            yes.set(DataComponents.DYED_COLOR, new DyedItemColor(block.defaultMapColor().col));
            yes.set(DataComponents.ITEM_NAME, Component.translatable("item.ballistics.snortable").append(block.getName()));
            yes.set(DataComponents.CONSUMABLE, Consumables.defaultFood().consumeSeconds(Math.abs(block.defaultDestroyTime()/3)).animation(ItemUseAnimation.SPYGLASS).build());
            result = yes;
        }

        return result;
    }

    @Override
    public RecipeSerializer<SnortableRecipe> getSerializer() {
        return SnortableRecipeSerializer.INSTANCE;
    }
}
