package net.antopfr.advancedweather.weather.maps;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BiomeHumidityData {
    private static final Map<WeatherTypes, Float> DEFAULTS = new HashMap<>();

    static {
        // OVERWORLD
        DEFAULTS.put(WeatherTypes.CLEAR,          50.0f);
        DEFAULTS.put(WeatherTypes.SUNNY,          40.0f);
        DEFAULTS.put(WeatherTypes.CLOUDY,         65.0f);
        DEFAULTS.put(WeatherTypes.OVERCAST,       75.0f);
        DEFAULTS.put(WeatherTypes.MIST,           85.0f);
        DEFAULTS.put(WeatherTypes.FOG,            90.0f);
        DEFAULTS.put(WeatherTypes.DENSE_FOG,      95.0f);
        DEFAULTS.put(WeatherTypes.DRIZZLE,        88.0f);
        DEFAULTS.put(WeatherTypes.LIGHT_RAIN,     92.0f);
        DEFAULTS.put(WeatherTypes.HEAVY_RAIN,     98.0f);
        DEFAULTS.put(WeatherTypes.FREEZING_RAIN,  95.0f);
        DEFAULTS.put(WeatherTypes.THUNDERSTORM,   95.0f);
        DEFAULTS.put(WeatherTypes.SNOW,           80.0f);
        DEFAULTS.put(WeatherTypes.BLIZZARD,       85.0f);
        DEFAULTS.put(WeatherTypes.HAIL,           85.0f);
        DEFAULTS.put(WeatherTypes.WINDY,          45.0f);
        DEFAULTS.put(WeatherTypes.SANDSTORM,      10.0f);

        // NETHER
        DEFAULTS.put(WeatherTypes.ASH_STORM,       4.0f);
        DEFAULTS.put(WeatherTypes.BRIMSTONE_STORM, 1.0f);
        DEFAULTS.put(WeatherTypes.LAVA_RAIN,       8.0f);
        DEFAULTS.put(WeatherTypes.NETHERSTORM,     2.0f);
        DEFAULTS.put(WeatherTypes.HELLFIRE,        0.0f);

        // END
        DEFAULTS.put(WeatherTypes.VOID_STORM,      1.0f);
        DEFAULTS.put(WeatherTypes.END_MIST,        12.0f);
        DEFAULTS.put(WeatherTypes.CHORUS_GALE,     3.0f);
        DEFAULTS.put(WeatherTypes.ENDERSTORM,      2.0f);
    }

    private static final Map<String, Float> TABLE = new HashMap<>();

    static {
        // ════════════════════════════════════════════════════════════════════
        // HIGH HUMIDITY (Saturé / Aquatique / Marécageux)
        // ════════════════════════════════════════════════════════════════════
        humidity("jungle", 88.0f);
        humidity("bamboo_jungle", 85.0f);
        humidity("sparse_jungle", 78.0f);
        humidity("swamp", 92.0f);
        humidity("mangrove_swamp", 95.0f);
        humidity("lush_caves", 80.0f);

        humidity("ocean", 85.0f);
        humidity("deep_ocean", 85.0f);
        humidity("cold_ocean", 80.0f);
        humidity("deep_cold_ocean", 80.0f);
        humidity("frozen_ocean", 75.0f);
        humidity("deep_frozen_ocean", 75.0f);
        humidity("warm_ocean", 85.0f);
        humidity("lukewarm_ocean", 85.0f);
        humidity("deep_lukewarm_ocean", 85.0f);
        humidity("river", 78.0f);
        humidity("frozen_river", 75.0f);
        humidity("beach", 70.0f);

        // ════════════════════════════════════════════════════════════════════
        // TEMPERATE HUMIDITY (Zones forestières et plaines)
        // ════════════════════════════════════════════════════════════════════
        humidity("forest", 60.0f);
        humidity("birch_forest", 58.0f);
        humidity("old_growth_birch_forest", 60.0f);
        humidity("dark_forest", 68.0f);
        humidity("cherry_grove", 55.0f);
        humidity("plains", 50.0f);
        humidity("sunflower_plains", 48.0f);
        humidity("meadow", 55.0f);

        // ════════════════════════════════════════════════════════════════════
        // LOW HUMIDITY (Aride / Sec)
        // ════════════════════════════════════════════════════════════════════
        humidity("desert", 12.0f);
        humidity("badlands", 15.0f);
        humidity("eroded_badlands", 14.0f);
        humidity("wooded_badlands", 25.0f);
        humidity("savanna", 35.0f);
        humidity("savanna_plateau", 35.0f);
        humidity("windswept_savanna", 30.0f);

        // ════════════════════════════════════════════════════════════════════
        // COLD & MOUNTAIN HUMIDITY
        // ════════════════════════════════════════════════════════════════════
        humidity("taiga", 50.0f);
        humidity("old_growth_pine_taiga", 52.0f);
        humidity("old_growth_spruce_taiga", 55.0f);
        humidity("snowy_plains", 45.0f);
        humidity("snowy_slopes", 40.0f);
        humidity("snowy_taiga", 48.0f);
        humidity("snowy_beach", 60.0f);
        humidity("grove", 45.0f);
        humidity("frozen_peaks", 35.0f);
        humidity("jagged_peaks", 38.0f);
        humidity("stony_peaks", 40.0f);
        humidity("ice_spikes", 30.0f);

        humidity("windswept_hills", 45.0f);
        humidity("windswept_forest", 48.0f);
        humidity("windswept_gravelly_hills", 42.0f);
        humidity("stony_shore", 65.0f);
        humidity("mushroom_fields", 65.0f);
        humidity("dripstone_caves", 70.0f);

        // ════════════════════════════════════════════════════════════════════
        // NETHER (Sécheresse sub-totale)
        // ════════════════════════════════════════════════════════════════════
        humidity("nether_wastes", 0.0f);
        humidity("crimson_forest", 5.0f);
        humidity("warped_forest", 8.0f);
        humidity("soul_sand_valley", 0.0f);
        humidity("basalt_deltas", 0.0f);

        // ════════════════════════════════════════════════════════════════════
        // THE END (Vide absolu)
        // ════════════════════════════════════════════════════════════════════
        humidity("the_end", 1.0f);
        humidity("small_end_islands", 1.0f);
        humidity("end_midlands", 2.0f);
        humidity("end_highlands", 2.0f);
        humidity("end_barrens", 1.0f);
    }

    public static float getBaseHumidity(ResourceLocation biome, WeatherTypes type) {
        float weatherInfluence = DEFAULTS.getOrDefault(type, 60.0f);
        if (biome == null) return weatherInfluence;

        Float biomeBase = TABLE.get(biome.getPath());
        if (biomeBase == null) return weatherInfluence;

        if (type.isNether() || type.isEnd()) {
            return Math.max(biomeBase, weatherInfluence);
        } else {
            return (biomeBase + weatherInfluence) / 2.0f;
        }
    }

    private static void humidity(String biomePath, float baseHum) {
        TABLE.put(biomePath, baseHum);
    }
}