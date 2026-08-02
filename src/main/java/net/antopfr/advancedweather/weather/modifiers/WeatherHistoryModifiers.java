package net.antopfr.advancedweather.weather.modifiers;

import net.antopfr.advancedweather.weather.WeatherTypes;

import java.util.List;
import java.util.function.Predicate;

public class WeatherHistoryModifiers {

    public static float historyModifier(WeatherTypes target, List<WeatherTypes> recentHistory) {
        if (recentHistory.isEmpty()) return 1.0f;

        float modifier = 1.0f;

        WeatherTypes last = recentHistory.getLast();
        if (isExtreme(target) && target == last) {
            modifier *= 0.3f;
        }

        if (isEscalation(last, target)) {
            modifier *= 1.6f;
        }

        long dryStreak = countTrailingMatch(recentHistory, WeatherHistoryModifiers::isDry);
        if (dryStreak >= 3 && isWet(target)) {
            modifier *= 1.0f + (dryStreak - 2) * 0.25f;
        }

        long wetStreak = countTrailingMatch(recentHistory, WeatherHistoryModifiers::isWet);
        if (wetStreak >= 3 && isDry(target)) {
            modifier *= 1.0f + (wetStreak - 2) * 0.25f;
        }

        return modifier;
    }

    private static long countTrailingMatch(List<WeatherTypes> history, Predicate<WeatherTypes> predicate) {
        long count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (predicate.test(history.get(i))) count++;
            else break;
        }
        return count;
    }

    private static boolean isEscalation(WeatherTypes from, WeatherTypes to) {
        return switch (from) {
            case CLOUDY      -> to == WeatherTypes.OVERCAST;
            case OVERCAST    -> to == WeatherTypes.LIGHT_RAIN || to == WeatherTypes.SNOW;
            case LIGHT_RAIN  -> to == WeatherTypes.HEAVY_RAIN;
            case HEAVY_RAIN  -> to == WeatherTypes.THUNDERSTORM;
            case SNOW        -> to == WeatherTypes.BLIZZARD;
            case WINDY       -> to == WeatherTypes.SANDSTORM;
            case ASH_STORM   -> to == WeatherTypes.BRIMSTONE_STORM || to == WeatherTypes.NETHERSTORM;
            case VOID_STORM  -> to == WeatherTypes.ENDERSTORM;
            default -> false;
        };
    }

    private static boolean isExtreme(WeatherTypes type) {
        return switch (type) {
            case THUNDERSTORM, BLIZZARD, HAIL, SANDSTORM,
                 NETHERSTORM, BRIMSTONE_STORM, ENDERSTORM -> true;
            default -> false;
        };
    }

    private static boolean isDry(WeatherTypes type) {
        return switch (type) {
            case CLEAR, SUNNY, WINDY, CLOUDY, SANDSTORM -> true;
            default -> false;
        };
    }

    private static boolean isWet(WeatherTypes type) {
        return switch (type) {
            case DRIZZLE, LIGHT_RAIN, HEAVY_RAIN, THUNDERSTORM,
                 SNOW, BLIZZARD, FREEZING_RAIN, HAIL,
                 OVERCAST, FOG, DENSE_FOG, MIST -> true;
            default -> false;
        };
    }
}
