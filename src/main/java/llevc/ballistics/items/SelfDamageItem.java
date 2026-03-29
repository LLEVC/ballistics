package llevc.ballistics.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SelfDamageItem extends Item {
    public SelfDamageItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!level.isClientSide()) {
            player.hurtServer((ServerLevel) level, level.damageSources().source(DamageTypes.GENERIC),4.0f);
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,10,255));
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING,10,255));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
