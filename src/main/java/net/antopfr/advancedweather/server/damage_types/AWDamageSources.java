package net.antopfr.advancedweather.server.damage_types;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;

public class AWDamageSources {
    public static DamageSource hail(ServerLevel level) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(AWDamageTypes.HAIL)
        );
    }
}
