package llevc.ballistics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities implements ModInitializer {

    public static final EntityType<FlintlockProjectile> flintlockProjectile = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath("ballistics","flintlock_projectile"),
            EntityType.Builder.<FlintlockProjectile>of(FlintlockProjectile::new, MobCategory.MISC).sized(0.5f,0.5f).build(ResourceKey.create(Registries.ENTITY_TYPE,Identifier.fromNamespaceAndPath("ballistics","flintlock_projectile")))
    );

    @Override
    public void onInitialize() {
    }
}
