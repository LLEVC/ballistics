package llevc.ballistics;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class ModComponents {
    public static final DataComponentType<Unit> noWeaponConversion = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"no_weapon_throw_conversion"),
            DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(Unit.STREAM_CODEC).build()
    );
    protected static void initialize() {}
}
