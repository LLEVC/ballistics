package llevc.ballistics;

import llevc.ballistics.effects.Explode;
import llevc.ballistics.items.FireworkLauncherItem;
import llevc.ballistics.items.FlintlockItem;
import llevc.ballistics.items.PerfectParryItem;
import llevc.ballistics.items.SmokeItem;
import llevc.ballistics.recipes.SnortableRecipe;
import llevc.ballistics.recipes.SnortableRecipeSerializer;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.w3c.dom.Attr;

import javax.xml.crypto.Data;
import java.util.ArrayList;
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
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,BallisticsBakedItemGroupKey,BallisticsBakedItemGroup);

        ItemGroupEvents.modifyEntriesEvent(BallisticsItemGroupKey).register(itemGroup -> {
            itemGroup.accept(rhGlove);
            itemGroup.accept(FireworkLauncher);
            itemGroup.accept(Flintlock);
            itemGroup.accept(Ball);
            itemGroup.accept(flintlockHammer);
            itemGroup.accept(flintlockBarrel);
            itemGroup.accept(DrinkMixer);
            itemGroup.accept(EmptyFlask);
            itemGroup.accept(DragonsFlask);
            itemGroup.accept(PotionContents.createItemStack(ModItems.Flask, Potions.WATER));
            itemGroup.accept(pick);
            itemGroup.accept(cig);
            itemGroup.accept(vape);
            itemGroup.accept(cartridge);
            itemGroup.accept(snortable);
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
        ItemGroupEvents.modifyEntriesEvent(BallisticsBakedItemGroupKey).register(itemGroup -> {
            itemGroup.accept(pick);
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    itemGroup.accept(PotionContents.createItemStack(pick,Holder.direct(hey)));
                }
            }
            itemGroup.accept(cig);
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    ItemStack yo = PotionContents.createItemStack(cig,Holder.direct(hey));
                    yo.set(DataComponents.ITEM_NAME,Component.translatable("item.ballistics.custom_cig"));
                    itemGroup.accept(yo);
                }
            }
            itemGroup.accept(vape);
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    itemGroup.accept(PotionContents.createItemStack(vape,Holder.direct(hey)));
                }
            }
            itemGroup.accept(cartridge);
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    itemGroup.accept(PotionContents.createItemStack(cartridge,Holder.direct(hey)));
                }
            }
            itemGroup.accept(snortable);
            for (int i = 0; i < BuiltInRegistries.BLOCK.size(); i++) {
                Optional<Holder.Reference<Block>> wchat = BuiltInRegistries.BLOCK.get(i);
                if (wchat.isPresent()) {
                    Block hey = wchat.get().value();
                    itemGroup.accept(createSnortable(hey));
                }
            }
            for (int i = 0; i < BuiltInRegistries.POTION.size(); i++) {
                Optional<Holder.Reference<Potion>> wchat = BuiltInRegistries.POTION.get(i);
                if (wchat.isPresent()) {
                    Potion hey = wchat.get().value();
                    ItemStack yo = PotionContents.createItemStack(snortable,Holder.direct(hey));
                    yo.set(DataComponents.DYED_COLOR,new DyedItemColor(yo.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).getColorOr(CommonColors.WHITE)));
                    yo.set(DataComponents.ITEM_NAME, Component.translatable("item.ballistics.snortable").append(hey.name()));
                    itemGroup.accept(yo);
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
                    .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .component(DataComponents.DYED_COLOR,new DyedItemColor(CommonColors.WHITE))
    );
    public static final PotionContents cigContents = new PotionContents(Optional.empty(),Optional.empty(),List.of(
            new MobEffectInstance(MobEffects.REGENERATION,45*20),
            new MobEffectInstance(MobEffects.NAUSEA,45*20)
    ),Optional.empty());
    public static final Item cig = register(
            "cig",
            SmokeItem::new,
            new Item.Properties().durability(32).component(DataComponents.POTION_CONTENTS, cigContents).equippable(EquipmentSlot.HEAD)
    );
    public static final Item vape = register(
            "vape",
            SmokeItem::new,
            new Item.Properties().durability(64).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).repairable(ItemTags.REPAIRS_IRON_ARMOR)
    );
    public static final Item pick = register(
            "pick",
            SmokeItem::new,
            new Item.Properties().durability(8).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).equippable(EquipmentSlot.HEAD)
    );
    public static final Item cartridge = register(
            "cartridge",
            Item::new,
            new Item.Properties().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
    );
    public static ItemStack createSnortable(Block block) {
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
        return yes;
    }

    public static final ResourceKey<CreativeModeTab> BallisticsItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics"));
    public static final CreativeModeTab BallisticsItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(Items.FIREWORK_ROCKET)).title(Component.translatable("itemGroup.ballistics")).build();
    public static final ResourceKey<CreativeModeTab> BallisticsFlaskItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics_flasks"));
    public static final CreativeModeTab BallisticsFlaskItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(EmptyFlask)).title(Component.translatable("itemGroup.ballistics_flasks")).build();
    public static final ResourceKey<CreativeModeTab> BallisticsBakedItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "ballistics_geeked"));
    public static final CreativeModeTab BallisticsBakedItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(vape)).title(Component.translatable("itemGroup.ballistics_geeked")).build();
}
