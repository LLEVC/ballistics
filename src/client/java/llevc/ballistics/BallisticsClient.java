package llevc.ballistics;

import llevc.ballistics.networking.ServerboundChargePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class BallisticsClient implements ClientModInitializer {
	public static int throwStartTick = 0;
	public static boolean isCharging = false;

	public static void startCharging(LocalPlayer localPlayer) {
		Ballistics.LOGGER.info("start charge");
		throwStartTick = localPlayer.tickCount;
		isCharging = true;
	}

	public static void resetCharging() {
		Ballistics.LOGGER.info("reset charge");
		throwStartTick = 0;
		isCharging = false;
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ScreenEvents.AFTER_INIT.register((mc,screen,damn,so) -> {
			ClientTickEvents.START_CLIENT_TICK.register(client -> {
				if (client.player == null || client.gameMode == null) return;
				if (client.gameMode.isSpectator()) return;

				//Ballistics.LOGGER.info(String.valueOf(client.options.keyDrop.consumeClick()) + " consumeClick");
				//Ballistics.LOGGER.info(String.valueOf(client.options.keyDrop.isDown()) + " isDown");

				if (!isCharging && !client.options.keyDrop.isDown() && throwStartTick > 0) {
					resetCharging();
				} else if (client.options.keyDrop.isDown() && !isCharging) {
					startCharging(client.player);
				} else if (isCharging && !client.options.keyDrop.isDown()) {
					client.player.connection.handleCustomPayload(new ServerboundChargePacket(throwStartTick));
					client.gameMode.dropItem(client.player, false);
					isCharging = false;
				}
			});
		});
	}
}