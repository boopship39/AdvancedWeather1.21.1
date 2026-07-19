package net.antopfr.advancedweather.weather.maps;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BiomeTemperatureData {

    private static final Map<String, Float> TABLE = new HashMap<>();

    static {
        // ── FORESTS & TROPICALS ──
        temp("forest", 16.0f);
        temp("birch_forest", 14.0f);
        temp("old_growth_birch_forest", 13.0f);
        temp("dark_forest", 12.0f);
        temp("cherry_grove", 15.0f);
        temp("jungle", 28.0f);
        temp("sparse_jungle", 27.0f);
        temp("bamboo_jungle", 26.0f);

        // ── ARID & DRY ──
        temp("desert", 38.0f);
        temp("badlands", 34.0f);
        temp("eroded_badlands", 35.0f);
        temp("wooded_badlands", 29.0f);
        temp("savanna", 30.0f);
        temp("savanna_plateau", 27.0f);
        temp("windswept_savanna", 25.0f);

        // ── COLD & SNOWY ──
        temp("snowy_plains", -5.0f);
        temp("snowy_slopes", -8.0f);
        temp("snowy_taiga", -6.0f);
        temp("snowy_beach", -2.0f);
        temp("grove", -4.0f);
        temp("frozen_peaks", -15.0f);
        temp("jagged_peaks", -12.0f);
        temp("ice_spikes", -18.0f);

        // ── AQUATIC & TEMPERATE ──
        temp("plains", 18.0f);
        temp("sunflower_plains", 19.0f);
        temp("meadow", 14.0f);
        temp("swamp", 22.0f);
        temp("mangrove_swamp", 25.0f);
        temp("ocean", 14.0f);
        temp("deep_ocean", 10.0f);
        temp("cold_ocean", 7.0f);
        temp("deep_cold_ocean", 4.0f);
        temp("frozen_ocean", -4.0f);
        temp("deep_frozen_ocean", -6.0f);
        temp("warm_ocean", 24.0f);
        temp("lukewarm_ocean", 20.0f);
        temp("deep_lukewarm_ocean", 16.0f);
        temp("river", 15.0f);
        temp("frozen_river", -2.0f);
        temp("beach", 19.0f);

        // ── MOUNTAINS & CAVES ──
        temp("windswept_hills", 8.0f);
        temp("windswept_forest", 7.0f);
        temp("windswept_gravelly_hills", 6.0f);
        temp("stony_peaks", 4.0f);
        temp("stony_shore", 10.0f);
        temp("mushroom_fields", 17.0f);
        temp("dripstone_caves", 11.0f);
        temp("lush_caves", 16.0f);

        // ── NETHER ──
        temp("nether_wastes", 50.0f);
        temp("crimson_forest", 45.0f);
        temp("warped_forest", 38.0f);
        temp("soul_sand_valley", 32.0f);
        temp("basalt_deltas", 55.0f);

        // ── THE END ──
        temp("the_end", -15.0f);
        temp("small_end_islands", -18.0f);
        temp("end_midlands", -12.0f);
        temp("end_highlands", -10.0f);
        temp("end_barrens", -15.0f);
    }

    public static float getBiomeOffset(ResourceLocation biome) {
        if (biome == null) return 0f;
        Float biomeValue = TABLE.get(biome.getPath());
        if (biomeValue == null) return 0f;
        return (biomeValue - 15.0f) * 0.65f;
    }

    private static void temp(String biomePath, float baseTemp) {
        TABLE.put(biomePath, baseTemp);
    }
}