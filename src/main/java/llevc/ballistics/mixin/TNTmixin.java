package llevc.ballistics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import llevc.ballistics.FlintlockProjectile;
import net.fabricmc.fabric.impl.item.EnchantmentUtil;
import net.fabricmc.fabric.mixin.registry.sync.RegistriesMixin;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.function.Predicate;

@Mixin(PrimedTnt.class)
public abstract class TNTmixin {
    @Shadow
    public abstract int getFuse();

    @Shadow
    public abstract void setFuse(int time);

    @WrapMethod(method = "hurtServer")
    public boolean bro(ServerLevel level, DamageSource source, float damage, Operation<Boolean> original) {
        Boolean og = original.call(level,source,damage);
        if (og) return true;
        if (source.getWeaponItem() != null) {
            if (source.getWeaponItem().has(DataComponents.PIERCING_WEAPON)) {
                return false;
            }
        }
        int fuse = this.getFuse();
        int dmg = Math.round(damage);
        if (source.is(DamageTypeTags.IS_EXPLOSION)) dmg*=3;
        if (fuse-dmg > 0 && !(source.getDirectEntity() instanceof FlintlockProjectile)) {
            this.setFuse(fuse-dmg);
        } else if (fuse > 2) {
            this.setFuse(2);
        }
        return true;
    }
}
