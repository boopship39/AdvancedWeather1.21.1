package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.util.AWRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BiomeAtmosphereData {

    private static BiomeAtmosphere lookup(Level level, ResourceLocation biome) {
        if (biome == null) return BiomeAtmosphere.DEFAULT;
        Registry<BiomeAtmosphere> reg = level.registryAccess()
                .registry(AWRegistries.BIOME_ATMOSPHERE).orElse(null);
        if (reg == null) return BiomeAtmosphere.DEFAULT;
        BiomeAtmosphere v = reg.get(biome);
        return v != null ? v : BiomeAtmosphere.DEFAULT;
    }

    public static float getBiomeOffset(Level level, ResourceLocation biome) {
        return (lookup(level, biome).temperature() - 15.0f) * 0.65f;
    }

    public static float getHumidityOffset(Level level, ResourceLocation biome) {
        return lookup(level, biome).humidity() - 60.0f;
    }
}
