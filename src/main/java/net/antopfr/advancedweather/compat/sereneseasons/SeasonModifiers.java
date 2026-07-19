package net.antopfr.advancedweather.compat.sereneseasons;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.server.level.ServerLevel;

public class SeasonModifiers {

    private static final int SPRING = 0;
    private static final int SUMMER = 1;
    private static final int AUTUMN = 2;
    private static final int WINTER = 3;

    // TRANSITION PROBABILITY
    public static float seasonModifier(WeatherTypes target, ServerLevel level) {
        int season = SeasonCompat.getCurrentSeasonOrdinal(level);
        if (season == -1) return 1.0f;

        return switch (season) {
            case SPRING -> springModifier(target);
            case SUMMER -> summerModifier(target);
            case AUTUMN -> autumnModifier(target);
            case WINTER -> winterModifier(target);
            default -> 1.0f;
        };
    }

    private static float springModifier(WeatherTypes type) {
        return switch (type) {
            case LIGHT_RAIN, DRIZZLE, OVERCAST -> 1.6f;
            case THUNDERSTORM, HEAVY_RAIN      -> 1.3f;
            case SUNNY, CLEAR                  -> 1.1f;
            case SNOW, BLIZZARD, FREEZING_RAIN  -> 0.3f;
            case SANDSTORM                      -> 0.5f;
            default -> 1.0f;
        };
    }

    private static float summerModifier(WeatherTypes type) {
        return switch (type) {
            case SUNNY, CLEAR              -> 1.6f;
            case THUNDERSTORM, HAIL        -> 1.5f;
            case SANDSTORM                 -> 1.4f;
            case WINDY                     -> 1.2f;
            case SNOW, BLIZZARD, FREEZING_RAIN -> 0.05f;
            case FOG, DENSE_FOG            -> 0.6f;
            default -> 1.0f;
        };
    }

    private static float autumnModifier(WeatherTypes type) {
        return switch (type) {
            case FOG, DENSE_FOG, MIST      -> 1.7f;
            case OVERCAST, CLOUDY          -> 1.4f;
            case LIGHT_RAIN, HEAVY_RAIN, DRIZZLE -> 1.4f;
            case WINDY                     -> 1.3f;
            case THUNDERSTORM              -> 1.1f;
            case SUNNY                     -> 0.7f;
            case SANDSTORM                 -> 0.3f;
            default -> 1.0f;
        };
    }

    private static float winterModifier(WeatherTypes type) {
        return switch (type) {
            case SNOW, BLIZZARD            -> 1.8f;
            case FREEZING_RAIN, HAIL       -> 1.5f;
            case FOG, DENSE_FOG            -> 1.3f;
            case OVERCAST                  -> 1.3f;
            case SUNNY, SANDSTORM          -> 0.2f;
            case THUNDERSTORM              -> 0.4f;
            default -> 1.0f;
        };
    }

    // TEMP OFFSET

    public static float temperatureOffset(ServerLevel level) {
        int season = SeasonCompat.getCurrentSeasonOrdinal(level);
        return switch (season) {
            case SPRING -> -2f;
            case SUMMER -> 6f;
            case AUTUMN -> -3f;
            case WINTER -> -10f;
            default -> 0f; // -1 (indisponible) ou cas imprévu
        };
    }

    // DEW POINT OFFSET

    public static float dewPointOffset(ServerLevel level) {
        int season = SeasonCompat.getCurrentSeasonOrdinal(level);
        return switch (season) {
            case SPRING -> 1f;
            case SUMMER -> 3f;
            case AUTUMN -> 0f;
            case WINTER -> -5f;
            default -> 0f;
        };
    }

    public static float durationModifier(WeatherTypes type, ServerLevel level) {
        int season = SeasonCompat.getCurrentSeasonOrdinal(level);
        if (season == -1) return 1.0f;

        return switch (season) {
            case WINTER -> switch (type) {
                case SNOW, BLIZZARD -> 1.4f;
                case SUNNY, CLEAR   -> 0.7f;
                default -> 1.0f;
            };
            case SUMMER -> switch (type) {
                case SUNNY, CLEAR   -> 1.3f;
                case THUNDERSTORM   -> 0.8f;
                default -> 1.0f;
            };
            default -> 1.0f;
        };
    }

    public static boolean isSeasonalType(WeatherTypes t) {
        return switch (t) {
            case SNOW, BLIZZARD, FREEZING_RAIN -> true;
            case SANDSTORM, HAIL -> true;
            default -> false;
        };
    }
}