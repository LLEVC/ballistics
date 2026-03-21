package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireworkRocketItem.class)
public class FireworkItemMixin {
    @WrapMethod(method = "use")
    public InteractionResult init(Level level, Player player, InteractionHand interactionHand, Operation<InteractionResult> original) {
        if (player.isVisuallyCrawling()) {
            player.startFallFlying();
        }
        return original.call(level, player, interactionHand);
    }
}
