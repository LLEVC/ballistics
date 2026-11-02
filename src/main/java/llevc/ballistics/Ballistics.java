package llevc.ballistics;

import llevc.ballistics.networking.ClientboundJumpPacket;
import llevc.ballistics.networking.ServerboundChargePacket;
import llevc.ballistics.recipes.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ballistics implements ModInitializer {
	public static final String MOD_ID = "ballistics";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static int getMaxChargeTime(ItemStack itemStack, LivingEntity livingEntity) {
		float cooldown = BallisticsEnchantHelper.getThrowCooldown(itemStack,livingEntity, 40 + Math.round((float)(40 * (1/(livingEntity.getAttribute(Attributes.ATTACK_SPEED) != null ? livingEntity.getAttribute(Attributes.ATTACK_SPEED).getValue() : 4)))-10));
		if (!livingEntity.getOffhandItem().isEmpty()) cooldown = BallisticsEnchantHelper.getThrowCooldown(livingEntity.getOffhandItem(),livingEntity,cooldown);
		return (int) Math.floor(cooldown);
	}

	public static int getMaxOvercharge(ItemStack itemStack, LivingEntity livingEntity) {
		int overcharge = (int) BallisticsEnchantHelper.getOverchargeAmount(itemStack,livingEntity, BallisticsEnchantHelper.getOverchargeAmount(livingEntity.getOffhandItem(),livingEntity,0));
		if (!(EnchantmentHelper.has(livingEntity.getOffhandItem().isEmpty() ? itemStack : livingEntity.getOffhandItem(), ModEnchantmentEffects.overchargeAsPower))) overcharge = Math.clamp(overcharge, 0, itemStack.isEmpty() ? 0 : (itemStack.count()-1));
		return overcharge;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();
		ModSounds.initialize();
		ModDamageTypes.initialize();
		ModEnchantmentEffects.init();

		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_flask"), FlaskRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_drink_mixer"), DrinkMixerRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_mixed_drink"), MixedDrinkRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_flintlock_load"), FlintlockLoadRecipe.SERIALIZER);

		PayloadTypeRegistry.serverboundPlay().register(ServerboundChargePacket.TYPE,ServerboundChargePacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ClientboundJumpPacket.TYPE,ClientboundJumpPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ServerboundChargePacket.TYPE, (payload, context) -> {
			if (payload.integer() == 0) {
				((PlayerInterface) context.player()).ballistics$resetThrowCharge();
			} else {
				((PlayerInterface) context.player()).ballistics$startThrowCharge(context.player().tickCount);
			}
		});

		LOGGER.info("if you read this you're prolly gonna be touched within the next 16 hours");
	}
}