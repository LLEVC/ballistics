package llevc.ballistics;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.rmi.registry.Registry;

public class ModDamageTypes {

    public static ResourceKey<DamageType> lungCancer = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("ballistics","lungs"));

    public static void initialize() {
    }

}
