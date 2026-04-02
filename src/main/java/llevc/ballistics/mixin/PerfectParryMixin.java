package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.Ballistics;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(BlocksAttacks.class)
public class PerfectParryMixin {
    @WrapMethod(method = "onBlocked")
    public void init(ServerLevel serverLevel, LivingEntity livingEntity, Operation<Void> original) {
        Ballistics.LOGGER.info(String.valueOf(livingEntity.getTicksUsingItem()));
        if (livingEntity.getTicksUsingItem() < 20) {
            List<Player> yuh = serverLevel.getNearbyPlayers(TargetingConditions.forCombat(),livingEntity,livingEntity.getBoundingBox().inflate(10,2,10));
            for (Player player : yuh) {
                Ballistics.LOGGER.info(player.getPlainTextName());
                player.getCooldowns().addCooldown(livingEntity.getMainHandItem(),10*20);
            }
        }
        original.call(serverLevel,livingEntity);
    }
}
