package llevc.ballistics;

import llevc.ballistics.items.FireworkLauncherItem;
import llevc.ballistics.items.FlintlockItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.block.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModItems {
    public static <GenericItem extends Item> GenericItem register(String name, Function<Item.Properties, GenericItem> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Ballistics.MOD_ID, name));
        GenericItem item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
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
    );
    public static final Item rhGlove = register(
            "rh_glove", //stands for Rocket Handling Glove btw
            Item::new,
            new Item.Properties().durability(64).repairable(ItemTags.REPAIRS_LEATHER_ARMOR).enchantable(15)
    );
    public static final Item Flintlock = register(
            "flintlock",
            FlintlockItem::new,
            new Item.Properties()
                    .durability(24)
                    .enchantable(1)
                    .repairable(ItemTags.REPAIRS_IRON_ARMOR)
                    .component(DataComponents.WEAPON,new Weapon(1,1))
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,1.0f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,-2.0f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build())
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
    public static final Item FireworkLauncher = register(
            "firework_launcher",
            FireworkLauncherItem::new,
            new Item.Properties().stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1).component(DataComponents.LORE,new ItemLore(List.of(Component.literal("literally just a crossbow which only accepts fireworks lmao"))))
    );

    //potions
    public static final Potion MixedPotion = Registry.register(
            BuiltInRegistries.POTION,
            Identifier.fromNamespaceAndPath(Ballistics.MOD_ID, "mixed"),
            new Potion("mixed")
    );

    public static final ResourceKey<CreativeModeTab> BallisticsItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics"));
    public static final CreativeModeTab BallisticsItemGroup = FabricCreativeModeTab.builder().icon(() -> new ItemStack(Items.FIREWORK_ROCKET)).displayItems((params, output) -> {
        output.accept(rhGlove);
        output.accept(FireworkLauncher);
        output.accept(Flintlock);
        output.accept(Ball);
        output.accept(flintlockHammer);
        output.accept(flintlockBarrel);
        output.accept(DrinkMixer);
        output.accept(EmptyFlask);
        output.accept(DragonsFlask);
        output.accept(PotionContents.createItemStack(ModItems.Flask, Potions.WATER));
    }).title(Component.translatable("itemGroup.ballistics")).build();

    public static final ResourceKey<CreativeModeTab> BallisticsFlaskItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics_flasks"));
    public static final CreativeModeTab BallisticsFlaskItemGroup = FabricCreativeModeTab.builder().icon(() -> new ItemStack(EmptyFlask)).displayItems((params,output) -> {
        output.accept(DrinkMixer);
        output.accept(EmptyFlask);
        output.accept(DragonsFlask);
        for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
            Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
            if (wchat.isPresent()) {
                Potion hey = wchat.get().value();
                output.accept(PotionContents.createItemStack(Flask,Holder.direct(hey)));
            }
        }}).title(Component.translatable("itemGroup.ballistics_flasks")).build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,BallisticsItemGroupKey,BallisticsItemGroup);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,BallisticsFlaskItemGroupKey,BallisticsFlaskItemGroup);
    }
}
