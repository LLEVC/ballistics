package llevc.ballistics;

import llevc.ballistics.effects.Explode;
import llevc.ballistics.items.FlintlockItem;
import llevc.ballistics.items.PerfectParryItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModItems {
    public static <GenericItem extends Item> GenericItem register(String name, Function<Item.Properties, GenericItem> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, name));
        GenericItem item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,BallisticsItemGroupKey,BallisticsItemGroup);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,BallisticsFlaskItemGroupKey,BallisticsFlaskItemGroup);

        ItemGroupEvents.modifyEntriesEvent(BallisticsItemGroupKey).register(itemGroup -> {
            itemGroup.accept(rhGlove);
            itemGroup.accept(Ball);
            itemGroup.accept(Flintlock);
            itemGroup.accept(flintlockHammer);
            itemGroup.accept(flintlockBarrel);
            itemGroup.accept(DrinkMixer);
            itemGroup.accept(EmptyFlask);
            itemGroup.accept(DragonsFlask);
            itemGroup.accept(PotionContents.createItemStack(ModItems.Flask, Potions.WATER));
        });
        ItemGroupEvents.modifyEntriesEvent(BallisticsFlaskItemGroupKey).register(itemGroup -> {
            itemGroup.accept(DrinkMixer);
            itemGroup.accept(EmptyFlask);
            itemGroup.accept(DragonsFlask);
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    itemGroup.accept(PotionContents.createItemStack(ModItems.Flask,Holder.direct(hey)));
                }
            }
        });
    }

    public static final Item EmptyFlask = register(
            "empty_flask",
            BottleItem::new,
            new Item.Properties().stacksTo(16)
    );
    public static final Item DragonsFlask = register(
            "dragon_flask",
            Item::new,
            new Item.Properties().craftRemainder(EmptyFlask).rarity(Rarity.UNCOMMON)
    );
    public static final Item Flask = register(
            "flask",
            PotionItem::new,
            new Item.Properties()
                    .stacksTo(4)
                    .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .component(DataComponents.CONSUMABLE, Consumables.defaultDrink().consumeSeconds(0.8f).build())
                    .usingConvertsTo(EmptyFlask).craftRemainder(EmptyFlask)
    );
    public static final Item DrinkMixer = register(
            "drink_mixer",
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .craftRemainder(ModItems.DrinkMixer)
    );
    public static final Item rhGlove = register(
            "rh_glove", //stands for Rocket Handling Glove btw
            Item::new,
            new Item.Properties().durability(64).repairable(ItemTags.REPAIRS_LEATHER_ARMOR).enchantable(15)
    );
    public static final Item Flintlock = register(
            "flintlock",
            FlintlockItem::new,
            new Item.Properties().durability(24).enchantable(1).repairable(ItemTags.REPAIRS_IRON_ARMOR)
    );
    public static final Item Ball = register(
            "ball",
            Item::new,
            new Item.Properties()
    );
    public static final Item flintlockBarrel = register(
            "flintlock_barrel",
            Item::new,
            new Item.Properties()
    );
    public static final Item flintlockHammer = register(
            "flintlock_hammer",
            Item::new,
            new Item.Properties()
    );

    //potions
    public static final Potion MixedPotion = Registry.register(
            BuiltInRegistries.POTION,
            ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "mixed"),
            new Potion("mixed")
    );
    public static final Holder<MobEffect> ExplodePotionEffect =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "explode"), new Explode(MobEffectCategory.HARMFUL, CommonColors.SOFT_RED));
    public static final Potion ExplosionPotion = Registry.register(
            BuiltInRegistries.POTION,
            ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "explode"),
            new Potion("explode",new MobEffectInstance(ExplodePotionEffect))
    );

    //the baked shi
    public static final Item snortable = register(
            "snortable",
            Item::new,
            new Item.Properties().stacksTo(16)
                    .component(DataComponents.CONSUMABLE, Consumables.defaultFood().consumeSeconds(0.8f).animation(ItemUseAnimation.SPYGLASS).build())
                    .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.DYED_COLOR,true))
    );

    public static final ResourceKey<CreativeModeTab> BallisticsItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics"));
    public static final CreativeModeTab BallisticsItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(Items.FIREWORK_ROCKET)).title(Component.translatable("itemGroup.ballistics")).build();
    public static final ResourceKey<CreativeModeTab> BallisticsFlaskItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics_flasks"));
    public static final CreativeModeTab BallisticsFlaskItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(EmptyFlask)).title(Component.translatable("itemGroup.ballistics_flasks")).build();
}
