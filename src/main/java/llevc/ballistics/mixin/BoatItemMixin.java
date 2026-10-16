package llevc.ballistics.mixin;

import llevc.ballistics.EntitySpawnItemInterface;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BoatItem.class)
public class BoatItemMixin implements EntitySpawnItemInterface {
    @Shadow
    @Final
    private EntityType<? extends AbstractBoat> entityType;

    @Unique
    public EntityType<?> ballistics$getSpawnEntity() {
        return entityType;
    }
}
