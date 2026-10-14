package llevc.ballistics.mixin;

import llevc.ballistics.StandingWallBlockItemInterface;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StandingAndWallBlockItem.class)
public class StandingWallBlockItemMixin implements StandingWallBlockItemInterface {
    @Shadow
    @Final
    protected Block wallBlock;

    @Override
    public Block ballistics$getWallBlock() {
        return wallBlock;
    }
}
