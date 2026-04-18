package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Objects;

@Mixin(FireworkRocketItem.class)
public class FireworkItemMixin extends Item {
    public FireworkItemMixin(Properties properties) {
        super(properties);
    }

    @WrapMethod(method = "useOn")
    public InteractionResult init(UseOnContext useOnContext, Operation<InteractionResult> original) {
        if (useOnContext.getPlayer() != null && (useOnContext.getPlayer().isHolding(ModItems.rhGlove) || useOnContext.getPlayer().isVisuallyCrawling())) {
            Fireworks fireworks = useOnContext.getItemInHand().getOrDefault(DataComponents.FIREWORKS, new Fireworks(1, List.of()));
            if (fireworks.explosions().isEmpty()) {
                return InteractionResult.PASS;
            }
        }
        return original.call(useOnContext);
    }

    @WrapMethod(method = "use")
    public InteractionResult init(Level level, Player player, InteractionHand interactionHand, Operation<InteractionResult> original) {
        EquipmentSlot oppositeHand = EquipmentSlot.OFFHAND;
        if (interactionHand.asEquipmentSlot() == EquipmentSlot.OFFHAND) {
            oppositeHand = EquipmentSlot.MAINHAND;
        }
        if (player.isVisuallyCrawling() || player.getItemBySlot(oppositeHand).is(ModItems.rhGlove)) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            if (level instanceof ServerLevel serverLevel) {
                if (player.dropAllLeashConnections(null)) {
                    level.playSound(null, player, SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }

                Projectile.spawnProjectile(new FireworkRocketEntity(level, itemStack, player), serverLevel, itemStack);
                itemStack.consume(1, player);
                player.awardStat(Stats.ITEM_USED.get(this));

                if (player.getItemBySlot(oppositeHand).is(ModItems.rhGlove)) {
                    player.getItemBySlot(oppositeHand).hurtAndBreak(1, Objects.requireNonNull(player.asLivingEntity()),oppositeHand);
                }
            }

            return InteractionResult.SUCCESS;
        }
        return original.call(level, player, interactionHand);
    }
}
