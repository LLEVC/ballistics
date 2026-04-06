package llevc.ballistics;

import llevc.ballistics.recipes.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FlaskRecipeSerializer.ID, FlaskRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, DrinkMixerRecipeSerializer.ID, DrinkMixerRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, MixedDrinkRecipeSerializer.ID, MixedDrinkRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, SnortableRecipeSerializer.ID, SnortableRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PickRecipeSerializer.ID, PickRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CartridgeRecipeSerializer.ID, CartridgeRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, GeekedRecipeSerializer.ID, GeekedRecipeSerializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FlintlockLoadRecipeSerializer.ID, FlintlockLoadRecipeSerializer.INSTANCE);

		LOGGER.info("if you read this you're prolly gonna be touched within the next 16 hours");
	}
}