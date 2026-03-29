package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.Ballistics;
import llevc.ballistics.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(BottleItem.class)
public class BottleItemMixin extends Item {
    public BottleItemMixin(Properties properties) {
        super(properties);
    }

    @WrapMethod(method = "use")
    public InteractionResult init(Level level, Player player, InteractionHand interactionHand, Operation<InteractionResult> original) {
        if (player.getItemInHand(interactionHand).is(ModItems.EmptyFlask)) {
            List<AreaEffectCloud> list = level.getEntitiesOfClass(
                    AreaEffectCloud.class,
                    player.getBoundingBox().inflate(2.0),
                    areaEffectCloud -> areaEffectCloud != null && areaEffectCloud.isAlive() && areaEffectCloud.getOwner() instanceof EnderDragon
            );
            Ballistics.LOGGER.info("ayo we good");
            if (!list.isEmpty()) {
                Ballistics.LOGGER.info("dragon");
                return InteractionResult.SUCCESS.heldItemTransformedTo(ModItems.DragonsFlask.getDefaultInstance());
            } else {
                BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    if (blockHitResult.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockPos)) {
                        if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
                            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                            level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);

                            Ballistics.LOGGER.info("booom");
                            ItemStack newItem = PotionContents.createItemStack(ModItems.Flask,Potions.WATER);
                            return InteractionResult.SUCCESS.heldItemTransformedTo(newItem);
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        }
        return original.call(level, player, interactionHand);
    }
}
