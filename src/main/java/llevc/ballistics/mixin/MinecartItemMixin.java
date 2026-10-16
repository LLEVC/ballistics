package llevc.ballistics.mixin;

import llevc.ballistics.EntitySpawnItemInterface;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecartItem.class)
public class MinecartItemMixin implements EntitySpawnItemInterface {
    @Shadow
    @Final
    private EntityType<? extends AbstractMinecart> type;

    @Unique
    public EntityType<?> ballistics$getSpawnEntity() {
        return type;
    }
}
