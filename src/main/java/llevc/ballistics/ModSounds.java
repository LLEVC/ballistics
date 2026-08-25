package llevc.ballistics;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Ballistics.MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static final SoundEvent FlintlockClick = registerSound("flintlock_click");
    public static final SoundEvent FlintlockShoot = registerSound("flintlock_shoot");
    public static final SoundEvent FlintlockDryShoot = registerSound("flintlock_dry_shoot");
    public static final SoundEvent FlintlockHit = registerSound("flintlock_hit");
    public static final SoundEvent FlintlockMiss = registerSound("flintlock_miss");

    public static void initialize() {
    }
}
