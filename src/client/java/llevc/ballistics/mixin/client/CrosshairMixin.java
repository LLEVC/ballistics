package llevc.ballistics.mixin.client;

import llevc.ballistics.BallisticsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Hud.class)
public abstract class CrosshairMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult);

    @Shadow
    @Final
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE;

    @Shadow
    @Final
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE;

    @Unique
    public float getUncappedChargeTime(LocalPlayer player) {
        return (float) (player.tickCount-BallisticsClient.throwStartTick)/(float) BallisticsClient.maxChargeTime;
    }
    @Unique
    public float getChargeTime(LocalPlayer player) {
        return Math.clamp(getUncappedChargeTime(player),0.0f,1.0f);
    }

    public float holy = 0f;

    @Redirect(method="extractCrosshair", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    public float bro(LocalPlayer player, float v) {
        holy = getChargeTime(player);
        return BallisticsClient.isCharging ? holy : player.getAttackStrengthScale(v);
    }

    @Inject(method = "extractCrosshair", at = @At("TAIL"))
    public void whyy(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.options.getCameraType().isFirstPerson()
                && (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult))
                && !this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)
                && (BallisticsClient.isCharging && holy >= 1f)
                && minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR
                && minecraft.player != null
        ) {
            //if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && this.minecraft.player.getAttackStrengthScale(0.0f) >= 1.0F) {
            //    AttackRange attackRange = (AttackRange)this.minecraft.player.getActiveItem().get(DataComponents.ATTACK_RANGE);
            //    boolean renderMaxAttackIndicator = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F
            //    && this.minecraft.crosshairPickEntity.isAlive()
            //    && (attackRange == null || attackRange.isInRange(this.minecraft.player, this.minecraft.hitResult.getLocation()));

            //    if (renderMaxAttackIndicator) return;
            //}

            graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, graphics.guiWidth() / 2 - 8, graphics.guiHeight() / 2 - 7 + 16, 16, 16);

            if (BallisticsClient.overchargeAmount > 0) {
                float chargeAmt = Math.clamp(getUncappedChargeTime(this.minecraft.player)-1f,0f,BallisticsClient.overchargeAmount);
                float overallProgress = (float) Math.floor(chargeAmt);
                float progress = (chargeAmt-overallProgress);
                List<Integer> colorList = List.of(CommonColors.GREEN,CommonColors.YELLOW,CommonColors.SOFT_RED);
                if (overallProgress > 0 && overallProgress != chargeAmt-1) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, graphics.guiWidth() / 2 - 8, graphics.guiHeight() / 2 - 7 + 16, 16, 16, colorList.get((int) ((overallProgress-1) % (colorList.size()))));
                } else if (overallProgress > 0 && overallProgress == chargeAmt-1) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, graphics.guiWidth() / 2 - 8, graphics.guiHeight() / 2 - 7 + 16, 16, 16, colorList.get((int) ((overallProgress) % (colorList.size()))));
                }
                if (BallisticsClient.overchargeAmount > chargeAmt) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, graphics.guiWidth() / 2 - 8, graphics.guiHeight() / 2 - 7 + 16, (int) ((progress)*17f), 4, colorList.get((int) (overallProgress % (colorList.size()))));
            }
        }
    }
}
