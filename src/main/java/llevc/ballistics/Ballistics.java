package llevc.ballistics;

import llevc.ballistics.recipes.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ballistics implements ModInitializer {
	public static final String MOD_ID = "ballistics";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();
		ModSounds.initialize();
		ModDamageTypes.initialize();

		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_flask"), FlaskRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_drink_mixer"), DrinkMixerRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_mixed_drink"), MixedDrinkRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_snortable"), SnortableRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_pick"), PickRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_cartridge_fill"), CartridgeRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_geeked"), GeekedRecipe.SERIALIZER);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("ballistics","crafting_special_flintlock_load"), FlintlockLoadRecipe.SERIALIZER);

		LOGGER.info("if you read this you're prolly gonna be touched within the next 16 hours");
	}
}