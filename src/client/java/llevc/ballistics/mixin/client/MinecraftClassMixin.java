package llevc.ballistics.mixin.client;

import llevc.ballistics.networking.ServerboundChargePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClassMixin {
	@Shadow
	@Nullable
	public MultiPlayerGameMode gameMode;

	@Unique

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;dropItem(Lnet/minecraft/client/player/LocalPlayer;Z)V"), method = "handleKeybinds")
	private void init(MultiPlayerGameMode instance, LocalPlayer player, boolean all) {
		//if (all) {
		//	player.connection.handleCustomPayload(new ServerboundChargePacket(0));
		//	instance.dropItem(player,true);
		//}
	}
}