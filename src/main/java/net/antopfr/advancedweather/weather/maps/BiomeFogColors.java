package net.antopfr.advancedweather.weather.maps;

import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BiomeFogColors {
    private static final Map<WeatherTypes, Integer> DEFAULTS = new HashMap<>();

    static {
        AWClientConfig config = AWClientConfig.get();

        // OVERWORLD
        DEFAULTS.put(WeatherTypes.FOG,          0xBFBFBF);
        DEFAULTS.put(WeatherTypes.DENSE_FOG,    0xA6A6AD);
        DEFAULTS.put(WeatherTypes.THUNDERSTORM, 0x596173);
        DEFAULTS.put(WeatherTypes.SANDSTORM,    0xD9B05C);
        DEFAULTS.put(WeatherTypes.HAIL,         0xCECFAB);
        DEFAULTS.put(WeatherTypes.MIST,         0xC8CDD0);

        // NETHER
        DEFAULTS.put(WeatherTypes.ASH_STORM,       0x54545C);
        DEFAULTS.put(WeatherTypes.BRIMSTONE_STORM, 0x884422);
        DEFAULTS.put(WeatherTypes.LAVA_RAIN,       0xAA4400);
        DEFAULTS.put(WeatherTypes.NETHERSTORM,     0x662200);
        DEFAULTS.put(WeatherTypes.HELLFIRE,        0xCC3300);

        // END
        DEFAULTS.put(WeatherTypes.VOID_STORM,      0x05000A);
        DEFAULTS.put(WeatherTypes.END_MIST,        0x4D225E);
        DEFAULTS.put(WeatherTypes.CHORUS_GALE,     0x3A0B4E);
        DEFAULTS.put(WeatherTypes.ENDERSTORM,      0x16002A);
    }

    private static final Map<String, Integer> TABLE = new HashMap<>();

    static {
        AWClientConfig config = AWClientConfig.get();

        // ════════════════════════════════════════════════════════════════════
        // FOREST
        // ════════════════════════════════════════════════════════════════════
        fog("forest",                      WeatherTypes.FOG,          0xA8B8A8);
        fog("forest",                      WeatherTypes.DENSE_FOG,    0x748274);
        fog("forest",                      WeatherTypes.THUNDERSTORM, 0x353D33);
        fog("forest",                      WeatherTypes.MIST,         0xB0BAB0);

        fog("birch_forest",                WeatherTypes.FOG,          0xB0BEB0);
        fog("birch_forest",                WeatherTypes.DENSE_FOG,    0x7A8C7A);
        fog("birch_forest",                WeatherTypes.THUNDERSTORM, 0x383E36);

        fog("old_growth_birch_forest",     WeatherTypes.FOG,          0xB0BEB0);
        fog("old_growth_birch_forest",     WeatherTypes.DENSE_FOG,    0x7A8C7A);
        fog("old_growth_birch_forest",     WeatherTypes.THUNDERSTORM, 0x383E36);

        fog("dark_forest",                 WeatherTypes.FOG,          0x8A9A82);
        fog("dark_forest",                 WeatherTypes.DENSE_FOG,    0x586050);
        fog("dark_forest",                 WeatherTypes.THUNDERSTORM, 0x252C22);

        fog("cherry_grove",                WeatherTypes.FOG,          0xCCB0BB);
        fog("cherry_grove",                WeatherTypes.DENSE_FOG,    0xA8889A);
        fog("cherry_grove",                WeatherTypes.THUNDERSTORM, 0x4A3845);

        // ════════════════════════════════════════════════════════════════════
        // JUNGLE
        // ════════════════════════════════════════════════════════════════════
        fog("jungle",                      WeatherTypes.FOG,          0x8CB896);
        fog("jungle",                      WeatherTypes.DENSE_FOG,    0x5A7D62);
        fog("jungle",                      WeatherTypes.THUNDERSTORM, 0x243028);
        fog("jungle",                      WeatherTypes.MIST,         0xA0B8A0);

        fog("sparse_jungle",               WeatherTypes.FOG,          0x98BC9E);
        fog("sparse_jungle",               WeatherTypes.DENSE_FOG,    0x658A6C);
        fog("sparse_jungle",               WeatherTypes.THUNDERSTORM, 0x2A362C);

        fog("bamboo_jungle",               WeatherTypes.FOG,          0x8AB894);
        fog("bamboo_jungle",               WeatherTypes.DENSE_FOG,    0x587860);
        fog("bamboo_jungle",               WeatherTypes.THUNDERSTORM, 0x222E24);

        // ════════════════════════════════════════════════════════════════════
        // SWAMP
        // ════════════════════════════════════════════════════════════════════
        fog("swamp",                       WeatherTypes.FOG,          0x90946A);
        fog("swamp",                       WeatherTypes.DENSE_FOG,    0x626646);
        fog("swamp",                       WeatherTypes.THUNDERSTORM, 0x2C2E1E);
        fog("swamp",                       WeatherTypes.MIST,         0xA8AA90);

        fog("mangrove_swamp",              WeatherTypes.FOG,          0x8C9060);
        fog("mangrove_swamp",              WeatherTypes.DENSE_FOG,    0x5E6240);
        fog("mangrove_swamp",              WeatherTypes.THUNDERSTORM, 0x282A1A);
        fog("mangrove_swamp",              WeatherTypes.MIST,         0xA4A888);

        // ════════════════════════════════════════════════════════════════════
        // DESERT
        // ════════════════════════════════════════════════════════════════════
        fog("desert",                      WeatherTypes.FOG,          0xD4C49A);
        fog("desert",                      WeatherTypes.DENSE_FOG,    0xB8A478);
        fog("desert",                      WeatherTypes.THUNDERSTORM, 0x5C4E2E);
        fog("desert",                      WeatherTypes.SANDSTORM,    0xD9B05C);

        // ════════════════════════════════════════════════════════════════════
        // BADLANDS
        // ════════════════════════════════════════════════════════════════════
        fog("badlands",                    WeatherTypes.FOG,          0xC4906A);
        fog("badlands",                    WeatherTypes.DENSE_FOG,    0xA06848);
        fog("badlands",                    WeatherTypes.THUNDERSTORM, 0x4A2C18);
        fog("badlands",                    WeatherTypes.SANDSTORM,    0xAA632B);

        fog("eroded_badlands",             WeatherTypes.FOG,          0xC4906A);
        fog("eroded_badlands",             WeatherTypes.DENSE_FOG,    0xA06848);
        fog("eroded_badlands",             WeatherTypes.THUNDERSTORM, 0x4A2C18);
        fog("eroded_badlands",             WeatherTypes.SANDSTORM,    0xAA632B);

        fog("wooded_badlands",             WeatherTypes.FOG,          0xB8886A);
        fog("wooded_badlands",             WeatherTypes.DENSE_FOG,    0x966450);
        fog("wooded_badlands",             WeatherTypes.THUNDERSTORM, 0x44281C);
        fog("wooded_badlands",             WeatherTypes.SANDSTORM,    0xAA632B);

        // ════════════════════════════════════════════════════════════════════
        // SAVANNA
        // ════════════════════════════════════════════════════════════════════
        fog("savanna",                     WeatherTypes.FOG,          0xC8BA88);
        fog("savanna",                     WeatherTypes.DENSE_FOG,    0xA89C68);
        fog("savanna",                     WeatherTypes.THUNDERSTORM, 0x4C4428);

        fog("savanna_plateau",             WeatherTypes.FOG,          0xC8BA88);
        fog("savanna_plateau",             WeatherTypes.DENSE_FOG,    0xA89C68);
        fog("savanna_plateau",             WeatherTypes.THUNDERSTORM, 0x4C4428);

        fog("windswept_savanna",           WeatherTypes.FOG,          0xC4B880);
        fog("windswept_savanna",           WeatherTypes.DENSE_FOG,    0xA49C62);
        fog("windswept_savanna",           WeatherTypes.THUNDERSTORM, 0x484224);

        // ════════════════════════════════════════════════════════════════════
        // SNOWY
        // ════════════════════════════════════════════════════════════════════
        fog("snowy_plains",                WeatherTypes.FOG,          0xCCD8E0);
        fog("snowy_plains",                WeatherTypes.DENSE_FOG,    0xA8BCC8);
        fog("snowy_plains",                WeatherTypes.THUNDERSTORM, 0x3C4650);

        fog("snowy_slopes",                WeatherTypes.FOG,          0xCED8E4);
        fog("snowy_slopes",                WeatherTypes.DENSE_FOG,    0xAABECC);
        fog("snowy_slopes",                WeatherTypes.THUNDERSTORM, 0x3A4450);

        fog("snowy_taiga",                 WeatherTypes.FOG,          0xBACCD4);
        fog("snowy_taiga",                 WeatherTypes.DENSE_FOG,    0x98B0BC);
        fog("snowy_taiga",                 WeatherTypes.THUNDERSTORM, 0x343E46);

        fog("snowy_beach",                 WeatherTypes.FOG,          0xC4D4DC);
        fog("snowy_beach",                 WeatherTypes.DENSE_FOG,    0xA0B8C4);
        fog("snowy_beach",                 WeatherTypes.THUNDERSTORM, 0x383E46);

        fog("grove",                       WeatherTypes.FOG,          0xBACCD8);
        fog("grove",                       WeatherTypes.DENSE_FOG,    0x96B0C0);
        fog("grove",                       WeatherTypes.THUNDERSTORM, 0x323C46);

        fog("frozen_peaks",                WeatherTypes.FOG,          0xD0DCE8);
        fog("frozen_peaks",                WeatherTypes.DENSE_FOG,    0xACC0D0);
        fog("frozen_peaks",                WeatherTypes.THUNDERSTORM, 0x3A4452);

        fog("jagged_peaks",                WeatherTypes.FOG,          0xC8D4E0);
        fog("jagged_peaks",                WeatherTypes.DENSE_FOG,    0xA4B8C8);
        fog("jagged_peaks",                WeatherTypes.THUNDERSTORM, 0x38424E);

        fog("ice_spikes",                  WeatherTypes.FOG,          0xCCDCEC);
        fog("ice_spikes",                  WeatherTypes.DENSE_FOG,    0xA8C0D4);
        fog("ice_spikes",                  WeatherTypes.THUNDERSTORM, 0x3A4454);

        // ════════════════════════════════════════════════════════════════════
        // TAIGA
        // ════════════════════════════════════════════════════════════════════
        fog("taiga",                       WeatherTypes.FOG,          0xA8B8B8);
        fog("taiga",                       WeatherTypes.DENSE_FOG,    0x7A9090);
        fog("taiga",                       WeatherTypes.THUNDERSTORM, 0x303C3C);
        fog("taiga",                       WeatherTypes.MIST,         0xB0BCBC);

        fog("old_growth_pine_taiga",       WeatherTypes.FOG,          0xA4B4B0);
        fog("old_growth_pine_taiga",       WeatherTypes.DENSE_FOG,    0x748888);
        fog("old_growth_pine_taiga",       WeatherTypes.THUNDERSTORM, 0x2E3A38);

        fog("old_growth_spruce_taiga",     WeatherTypes.FOG,          0xA0B0AC);
        fog("old_growth_spruce_taiga",     WeatherTypes.DENSE_FOG,    0x708480);
        fog("old_growth_spruce_taiga",     WeatherTypes.THUNDERSTORM, 0x2C3836);

        // ════════════════════════════════════════════════════════════════════
        // PLAINS / MEADOW
        // ════════════════════════════════════════════════════════════════════
        fog("plains",                      WeatherTypes.FOG,          0xBEBEBE);
        fog("plains",                      WeatherTypes.DENSE_FOG,    0xA0A0A8);
        fog("plains",                      WeatherTypes.THUNDERSTORM, 0x565E6E);

        fog("sunflower_plains",            WeatherTypes.FOG,          0xC4C4A8);
        fog("sunflower_plains",            WeatherTypes.DENSE_FOG,    0xA4A488);
        fog("sunflower_plains",            WeatherTypes.THUNDERSTORM, 0x585E50);

        fog("meadow",                      WeatherTypes.FOG,          0xB4C4B0);
        fog("meadow",                      WeatherTypes.DENSE_FOG,    0x909E8C);
        fog("meadow",                      WeatherTypes.THUNDERSTORM, 0x3C4438);

        // ════════════════════════════════════════════════════════════════════
        // OCEAN
        // ════════════════════════════════════════════════════════════════════
        fog("ocean",                       WeatherTypes.FOG,          0x90A8BC);
        fog("ocean",                       WeatherTypes.DENSE_FOG,    0x607888);
        fog("ocean",                       WeatherTypes.THUNDERSTORM, 0x1E2E3C);
        fog("ocean",                       WeatherTypes.MIST,         0xA8BCC8);

        fog("deep_ocean",                  WeatherTypes.FOG,          0x7898B0);
        fog("deep_ocean",                  WeatherTypes.DENSE_FOG,    0x506878);
        fog("deep_ocean",                  WeatherTypes.THUNDERSTORM, 0x1A2834);

        fog("cold_ocean",                  WeatherTypes.FOG,          0x88A0B8);
        fog("cold_ocean",                  WeatherTypes.DENSE_FOG,    0x5C7888);
        fog("cold_ocean",                  WeatherTypes.THUNDERSTORM, 0x1C2C38);

        fog("deep_cold_ocean",             WeatherTypes.FOG,          0x7898B0);
        fog("deep_cold_ocean",             WeatherTypes.DENSE_FOG,    0x506878);
        fog("deep_cold_ocean",             WeatherTypes.THUNDERSTORM, 0x1A2834);

        fog("frozen_ocean",                WeatherTypes.FOG,          0xA0BCCC);
        fog("frozen_ocean",                WeatherTypes.DENSE_FOG,    0x7898A8);
        fog("frozen_ocean",                WeatherTypes.THUNDERSTORM, 0x28363E);

        fog("deep_frozen_ocean",           WeatherTypes.FOG,          0x90AEC0);
        fog("deep_frozen_ocean",           WeatherTypes.DENSE_FOG,    0x6888A0);
        fog("deep_frozen_ocean",           WeatherTypes.THUNDERSTORM, 0x222E38);

        fog("warm_ocean",                  WeatherTypes.FOG,          0x98B8C4);
        fog("warm_ocean",                  WeatherTypes.DENSE_FOG,    0x6A8C98);
        fog("warm_ocean",                  WeatherTypes.THUNDERSTORM, 0x20303A);

        fog("lukewarm_ocean",              WeatherTypes.FOG,          0x94B0C0);
        fog("lukewarm_ocean",              WeatherTypes.DENSE_FOG,    0x648898);
        fog("lukewarm_ocean",              WeatherTypes.THUNDERSTORM, 0x1E2E38);

        fog("deep_lukewarm_ocean",         WeatherTypes.FOG,          0x8AACBC);
        fog("deep_lukewarm_ocean",         WeatherTypes.DENSE_FOG,    0x5C8494);
        fog("deep_lukewarm_ocean",         WeatherTypes.THUNDERSTORM, 0x1C2C36);

        // ════════════════════════════════════════════════════════════════════
        // MOUNTAINS / WINDSWEPT
        // ════════════════════════════════════════════════════════════════════
        fog("windswept_hills",             WeatherTypes.FOG,          0xB4C2CC);
        fog("windswept_hills",             WeatherTypes.DENSE_FOG,    0x889AA8);
        fog("windswept_hills",             WeatherTypes.THUNDERSTORM, 0x343A42);

        fog("windswept_forest",            WeatherTypes.FOG,          0xA8BAC0);
        fog("windswept_forest",            WeatherTypes.DENSE_FOG,    0x7E94A0);
        fog("windswept_forest",            WeatherTypes.THUNDERSTORM, 0x30383E);

        fog("windswept_gravelly_hills",    WeatherTypes.FOG,          0xB2C0C8);
        fog("windswept_gravelly_hills",    WeatherTypes.DENSE_FOG,    0x8698A4);
        fog("windswept_gravelly_hills",    WeatherTypes.THUNDERSTORM, 0x323840);

        fog("stony_peaks",                 WeatherTypes.FOG,          0xB0BEC8);
        fog("stony_peaks",                 WeatherTypes.DENSE_FOG,    0x849298);
        fog("stony_peaks",                 WeatherTypes.THUNDERSTORM, 0x30363C);

        fog("stony_shore",                 WeatherTypes.FOG,          0xB0BCCC);
        fog("stony_shore",                 WeatherTypes.DENSE_FOG,    0x8494A8);
        fog("stony_shore",                 WeatherTypes.THUNDERSTORM, 0x2E3640);

        // ════════════════════════════════════════════════════════════════════
        // MUSHROOM
        // ════════════════════════════════════════════════════════════════════
        fog("mushroom_fields",             WeatherTypes.FOG,          0xA898C0);
        fog("mushroom_fields",             WeatherTypes.DENSE_FOG,    0x7A6E96);
        fog("mushroom_fields",             WeatherTypes.THUNDERSTORM, 0x2C2440);

        // ════════════════════════════════════════════════════════════════════
        // RIVER / BEACH
        // ════════════════════════════════════════════════════════════════════
        fog("river",                       WeatherTypes.FOG,          0xA8B8C4);
        fog("river",                       WeatherTypes.DENSE_FOG,    0x7E9098);
        fog("river",                       WeatherTypes.THUNDERSTORM, 0x2A343C);
        fog("river",                       WeatherTypes.MIST,         0xB8C4C8);

        fog("frozen_river",                WeatherTypes.FOG,          0xB8CCE0);
        fog("frozen_river",                WeatherTypes.DENSE_FOG,    0x90AAC0);
        fog("frozen_river",                WeatherTypes.THUNDERSTORM, 0x303C48);

        fog("beach",                       WeatherTypes.FOG,          0xB8C8CC);
        fog("beach",                       WeatherTypes.DENSE_FOG,    0x8CA0A8);
        fog("beach",                       WeatherTypes.THUNDERSTORM, 0x343C42);

        // ════════════════════════════════════════════════════════════════════
        // DRIPSTONE / LUSH CAVES
        // ════════════════════════════════════════════════════════════════════
        fog("dripstone_caves",             WeatherTypes.FOG,          0xBCB4A8);
        fog("dripstone_caves",             WeatherTypes.DENSE_FOG,    0x9A9088);
        fog("dripstone_caves",             WeatherTypes.THUNDERSTORM, 0x403C36);

        fog("lush_caves",                  WeatherTypes.FOG,          0xA0B89A);
        fog("lush_caves",                  WeatherTypes.DENSE_FOG,    0x748C70);
        fog("lush_caves",                  WeatherTypes.THUNDERSTORM, 0x2C3828);


        // ════════════════════════════════════════════════════════════════════
        // NETHER
        // ════════════════════════════════════════════════════════════════════
        fog("nether_wastes",    WeatherTypes.ASH_STORM,       0x423D3A);
        fog("nether_wastes",    WeatherTypes.BRIMSTONE_STORM, 0x8B4422);
        fog("nether_wastes",    WeatherTypes.LAVA_RAIN,       0xAA4400);
        fog("nether_wastes",    WeatherTypes.NETHERSTORM,     0x662200);
        fog("nether_wastes",    WeatherTypes.HELLFIRE,        0xCC3300);

        fog("crimson_forest",   WeatherTypes.ASH_STORM,       0x443A3A);
        fog("crimson_forest",   WeatherTypes.BRIMSTONE_STORM, 0xAA2222);
        fog("crimson_forest",   WeatherTypes.LAVA_RAIN,       0xCC2200);
        fog("crimson_forest",   WeatherTypes.NETHERSTORM,     0x881100);
        fog("crimson_forest",   WeatherTypes.HELLFIRE,        0xFF2200);

        fog("warped_forest",    WeatherTypes.ASH_STORM,       0x364244);
        fog("warped_forest",    WeatherTypes.BRIMSTONE_STORM, 0x336644);
        fog("warped_forest",    WeatherTypes.LAVA_RAIN,       0x448855);
        fog("warped_forest",    WeatherTypes.NETHERSTORM,     0x224433);
        fog("warped_forest",    WeatherTypes.HELLFIRE,        0x559966);

        fog("soul_sand_valley", WeatherTypes.ASH_STORM,       0x3A3F46);
        fog("soul_sand_valley", WeatherTypes.BRIMSTONE_STORM, 0x445566);
        fog("soul_sand_valley", WeatherTypes.LAVA_RAIN,       0x336677);
        fog("soul_sand_valley", WeatherTypes.NETHERSTORM,     0x223344);
        fog("soul_sand_valley", WeatherTypes.HELLFIRE,        0x334455);

        fog("basalt_deltas",    WeatherTypes.ASH_STORM,       0x3E3E42);
        fog("basalt_deltas",    WeatherTypes.BRIMSTONE_STORM, 0x775544);
        fog("basalt_deltas",    WeatherTypes.LAVA_RAIN,       0x996644);
        fog("basalt_deltas",    WeatherTypes.NETHERSTORM,     0x553322);
        fog("basalt_deltas",    WeatherTypes.HELLFIRE,        0xBB4422);

        // ════════════════════════════════════════════════════════════════════
        // END
        // ════════════════════════════════════════════════════════════════════
        fog("the_end",          WeatherTypes.VOID_STORM,      0x030008);
        fog("the_end",          WeatherTypes.END_MIST,        0x4A1F56);
        fog("the_end",          WeatherTypes.CHORUS_GALE,     0x2D053F);
        fog("the_end",          WeatherTypes.ENDERSTORM,      0x120024);

        fog("end_highlands",    WeatherTypes.VOID_STORM,      0x04000C);
        fog("end_highlands",    WeatherTypes.END_MIST,        0x5A2B6D);
        fog("end_highlands",    WeatherTypes.CHORUS_GALE,     0x3D0C54);
        fog("end_highlands",    WeatherTypes.ENDERSTORM,      0x180030);

        fog("end_midlands",     WeatherTypes.VOID_STORM,      0x03000A);
        fog("end_midlands",     WeatherTypes.END_MIST,        0x4D225E);
        fog("end_midlands",     WeatherTypes.CHORUS_GALE,     0x340A47);
        fog("end_midlands",     WeatherTypes.ENDERSTORM,      0x140028);

        fog("end_barrens",      WeatherTypes.VOID_STORM,      0x010005);
        fog("end_barrens",      WeatherTypes.END_MIST,        0x3B1847);
        fog("end_barrens",      WeatherTypes.CHORUS_GALE,     0x22052F);
        fog("end_barrens",      WeatherTypes.ENDERSTORM,      0x0A0016);

        fog("small_end_islands", WeatherTypes.VOID_STORM,      0x010004);
        fog("small_end_islands", WeatherTypes.END_MIST,        0x351440);
        fog("small_end_islands", WeatherTypes.CHORUS_GALE,     0x1D0228);
        fog("small_end_islands", WeatherTypes.ENDERSTORM,      0x080012);
    }

    public static int getColor(WeatherTypes type, ResourceLocation biome) {
        if (biome != null) {
            Integer color = TABLE.get(biome.getPath() + "|" + type.name());
            if (color != null) return color;
        }
        return DEFAULTS.getOrDefault(type, 0xBFBFBF);
    }

    private static void fog(String biomePath, WeatherTypes type, int color) {
        TABLE.put(biomePath + "|" + type.name(), color);
    }
}