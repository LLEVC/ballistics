package llevc.ballistics;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.List;
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
        ItemGroupEvents.modifyEntriesEvent(BallisticsItemGroupKey).register(itemGroup -> {
            itemGroup.accept(rhGlove);
            itemGroup.accept(EmptyFlask);
            itemGroup.accept(DragonsFlask);
            itemGroup.accept(Flask);
        });
    }

    public static final Item EmptyFlask = register(
            "empty_flask",
            BottleItem::new,
            new Item.Properties()
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
                    .stacksTo(1)
                    .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .component(DataComponents.CONSUMABLE, Consumables.defaultDrink().consumeSeconds(0.8f).build())
                    .usingConvertsTo(EmptyFlask)
    );
    public static final Item rhGlove = register(
            "rh_glove", //stands for Rocket Handling Glove btw
            Item::new,
            new Item.Properties()
    );

    public static final ResourceKey<CreativeModeTab> BallisticsItemGroupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Ballistics.MOD_ID, "item_group"));
    public static final CreativeModeTab BallisticsItemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(Items.FIREWORK_ROCKET)).title(Component.translatable("itemGroup.ballistics")).build();
}
