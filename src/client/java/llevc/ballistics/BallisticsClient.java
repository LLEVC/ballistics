package llevc.ballistics;

import llevc.ballistics.networking.ClientboundJumpPacket;
import llevc.ballistics.networking.ServerboundChargePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.client.player.ClientHotbarScrollEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;

import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class BallisticsClient implements ClientModInitializer {
	public static int throwStartTick = 0;
	public static boolean isCharging = false;
	public static int maxChargeTime = 40;

	public static void startCharging(LocalPlayer localPlayer) {
		//Ballistics.LOGGER.info("start charge");
		throwStartTick = localPlayer.tickCount;
		isCharging = true;
		ClientPlayNetworking.send(new ServerboundChargePacket(throwStartTick));
	}

	public static void resetCharging() {
		//Ballistics.LOGGER.info("reset charge");
		throwStartTick = 0;
		isCharging = false;
		ClientPlayNetworking.send(new ServerboundChargePacket(throwStartTick));
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRenderers.register(ModEntities.flintlockProjectile, ThrownItemRenderer::new);
		EntityRenderers.register(ModEntities.thrownItemProjectile, ThrownItemEntityRenderer::new);

		ClientPlayNetworking.registerGlobalReceiver(ClientboundJumpPacket.TYPE, (packet, context) -> {
			context.player().push(packet.vec3());
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null || client.gameMode == null) return;
			if (client.gameMode.isSpectator()) return;

			//Ballistics.LOGGER.info(String.valueOf(client.options.keyDrop.consumeClick()) + " consumeClick");
			//Ballistics.LOGGER.info(String.valueOf(client.options.keyDrop.isDown()) + " isDown");

			if (client.player.hasItemInSlot(EquipmentSlot.MAINHAND)) {
				if (!isCharging && !client.options.keyDrop.isDown() && throwStartTick > 0) {
					resetCharging();
				} else if (client.options.keyDrop.isDown() && !isCharging && !client.hasControlDown()) {
					startCharging(client.player);
				} else if (client.options.keyDrop.isDown() && !isCharging && client.hasControlDown()) {
					client.gameMode.dropItem(client.player, true);
				} else if (isCharging && !client.options.keyDrop.isDown()) {
					client.gameMode.dropItem(client.player, false);
					isCharging = false;
				}
			} else if (isCharging || throwStartTick > 0) {
				resetCharging();
			}
		});

		ClientHotbarScrollEvents.AFTER.register((inventory,currentSlot,newSlot,x,y) -> {
			if (isCharging) resetCharging();
		});
	}
}