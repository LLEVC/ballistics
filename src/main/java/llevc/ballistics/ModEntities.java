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
    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static final ResourceKey<EntityType<?>> flintlockProjectileKey = ResourceKey.create(Registries.ENTITY_TYPE,Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"flintlock_projectile"));
    public static final EntityType<FlintlockProjectile> flintlockProjectile = register(
            flintlockProjectileKey,
            EntityType.Builder.<FlintlockProjectile>of(FlintlockProjectile::new,MobCategory.MISC)
            .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).noLootTable()
    );

    public static final ResourceKey<EntityType<?>> thrownItemProjectileKey = ResourceKey.create(Registries.ENTITY_TYPE,Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"thrown_item"));
    public static final EntityType<ThrownItemProjectile> thrownItemProjectile = register(
            thrownItemProjectileKey,
            EntityType.Builder.<ThrownItemProjectile>of(ThrownItemProjectile::new,MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).noLootTable()
    );

    public static final ResourceKey<EntityType<?>> thrownWeaponProjectileKey = ResourceKey.create(Registries.ENTITY_TYPE,Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"thrown_weapon"));
    public static final EntityType<ThrownWeaponProjectile> thrownWeaponProjectile = register(
            thrownWeaponProjectileKey,
            EntityType.Builder.<ThrownWeaponProjectile>of(ThrownWeaponProjectile::new,MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).eyeHeight(0.13f).noLootTable()
    );

    @Override
    public void onInitialize() {
    }
}
