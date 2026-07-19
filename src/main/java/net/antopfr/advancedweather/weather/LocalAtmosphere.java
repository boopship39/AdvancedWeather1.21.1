package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.weather.maps.BiomeHumidityData;
import net.antopfr.advancedweather.weather.maps.BiomeTemperatureData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

public class LocalAtmosphere {

    private static final float LAPSE_RATE_PRESSURE = 0.12f;
    private static final float LAPSE_RATE_TEMP     = 0.05f;

    public static float getLocalTemperature(ServerLevel level, BlockPos pos) {
        WeatherManager manager = WeatherManager.get(level);
        AtmosphericSystem atmosphere = manager.getAtmosphere(level);

        float base = atmosphere.getTemperature();

        ResourceLocation biome = level.getBiome(pos).unwrapKey()
                .map(ResourceKey::location).orElse(null);
        float biomeOffset = BiomeTemperatureData.getBiomeOffset(biome);

        float deltaY = pos.getY() - 64f;
        return Mth.clamp(base + biomeOffset - deltaY * LAPSE_RATE_TEMP, -80f, 150f);
    }

    public static float getLocalPressure(ServerLevel level, BlockPos pos) {
        float base = WeatherManager.get(level).getAtmosphere(level).getPressure();
        float deltaY = pos.getY() - 64f;
        return Mth.clamp(base - deltaY * LAPSE_RATE_PRESSURE, 800f, 1200f);
    }

    public static float getLocalHumidity(ServerLevel level, BlockPos pos) {
        WeatherManager manager = WeatherManager.get(level);
        WeatherTypes weather = manager.getCurrentWeather(level);
        float base = manager.getAtmosphere(level).getHumidity();

        ResourceLocation biome = level.getBiome(pos).unwrapKey()
                .map(ResourceKey::location).orElse(null);
        float biomeOffset = biome != null
                ? BiomeHumidityData.getBaseHumidity(biome, weather) - 60.0f
                : 0f;

        return Mth.clamp(base + biomeOffset, 0f, 100f);
    }

    public static float getWindKmh(ServerLevel level) {
        float wi = WeatherManager.get(level).getWindIntensity(level);
        return wi * wi * 120f;
    }
}
