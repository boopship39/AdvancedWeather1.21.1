package net.antopfr.advancedweather.server.damage_types;

import net.antopfr.advancedweather.AdvancedWeather;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class AWDamageTypes {
    public static final ResourceKey<DamageType> HAIL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "hail"));
}
