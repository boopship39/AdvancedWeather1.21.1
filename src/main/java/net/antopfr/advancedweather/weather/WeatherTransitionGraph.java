package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.compat.sereneseasons.SeasonModifiers;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.network.toclient.TransitionProbabilitiesPacket;
import net.antopfr.advancedweather.weather.modifiers.AtmosphereModifiers;
import net.antopfr.advancedweather.weather.modifiers.WeatherHistoryModifiers;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class WeatherTransitionGraph {

    public record Transition(WeatherTypes target, float weight) {}

    private static final Map<WeatherTypes, Integer> MIN_DURATION = new EnumMap<>(WeatherTypes.class);
    private static final Map<WeatherTypes, Integer> MAX_DURATION = new EnumMap<>(WeatherTypes.class);

    private static final Map<WeatherTypes, List<Transition>> TRANSITIONS = new EnumMap<>(WeatherTypes.class);

    static {
        // OVERWORLD
        duration(WeatherTypes.CLEAR,          18000, 36000);
        duration(WeatherTypes.SUNNY,          12000, 24000);
        duration(WeatherTypes.CLOUDY,          6000, 24000);
        duration(WeatherTypes.OVERCAST,        6000, 18000);
        duration(WeatherTypes.MIST,            3000,  9000);
        duration(WeatherTypes.DRIZZLE,         3000, 12000);
        duration(WeatherTypes.LIGHT_RAIN,      6000, 18000);
        duration(WeatherTypes.HEAVY_RAIN,      4000, 18000);
        duration(WeatherTypes.FREEZING_RAIN,   2000,  6000);
        duration(WeatherTypes.THUNDERSTORM,    2000,  6000);
        duration(WeatherTypes.SNOW,            9000, 18000);
        duration(WeatherTypes.BLIZZARD,        3000,  9000);
        duration(WeatherTypes.HAIL,            1000,  3000);
        duration(WeatherTypes.FOG,             9000, 18000);
        duration(WeatherTypes.DENSE_FOG,       3000,  9000);
        duration(WeatherTypes.WINDY,           6000, 24000);
        duration(WeatherTypes.SANDSTORM,       3000, 12000);

        from(WeatherTypes.CLEAR,
                to(WeatherTypes.CLEAR,    3),
                to(WeatherTypes.SUNNY,    5),
                to(WeatherTypes.WINDY,    2),
                to(WeatherTypes.FOG,      1),
                to(WeatherTypes.MIST,     2),
                to(WeatherTypes.OVERCAST, 1)
        );

        from(WeatherTypes.SUNNY,
                to(WeatherTypes.CLEAR,    4),
                to(WeatherTypes.SUNNY,    2),
                to(WeatherTypes.CLOUDY,   4),
                to(WeatherTypes.WINDY,    2),
                to(WeatherTypes.MIST,     1)
        );

        from(WeatherTypes.CLOUDY,
                to(WeatherTypes.SUNNY,    3),
                to(WeatherTypes.CLEAR,    1),
                to(WeatherTypes.CLOUDY,   1),
                to(WeatherTypes.OVERCAST, 4),
                to(WeatherTypes.WINDY,    2),
                to(WeatherTypes.FOG,      1),
                to(WeatherTypes.MIST,     2)
        );

        from(WeatherTypes.OVERCAST,
                to(WeatherTypes.CLOUDY,       2),
                to(WeatherTypes.DRIZZLE,      3),
                to(WeatherTypes.LIGHT_RAIN,   3),
                to(WeatherTypes.SNOW,         1),
                to(WeatherTypes.FOG,          1),
                to(WeatherTypes.DENSE_FOG,    1)
        );

        from(WeatherTypes.HEAVY_RAIN,
                to(WeatherTypes.LIGHT_RAIN,    3),
                to(WeatherTypes.THUNDERSTORM,  1),
                to(WeatherTypes.OVERCAST,      2),
                to(WeatherTypes.HAIL,          1)
        );

        from(WeatherTypes.THUNDERSTORM,
                to(WeatherTypes.HEAVY_RAIN,   4),
                to(WeatherTypes.OVERCAST,     2),
                to(WeatherTypes.HAIL,         2)
        );

        from(WeatherTypes.FOG,
                to(WeatherTypes.SUNNY,        2),
                to(WeatherTypes.CLEAR,        2),
                to(WeatherTypes.CLOUDY,       2),
                to(WeatherTypes.DENSE_FOG,    1),
                to(WeatherTypes.DRIZZLE,      1),
                to(WeatherTypes.MIST,         2)
        );

        from(WeatherTypes.MIST,
                to(WeatherTypes.SUNNY,  2),
                to(WeatherTypes.CLEAR,  2),
                to(WeatherTypes.FOG,    1),
                to(WeatherTypes.CLOUDY, 2)
        );

        from(WeatherTypes.DRIZZLE,
                to(WeatherTypes.OVERCAST,     2),
                to(WeatherTypes.LIGHT_RAIN,   3),
                to(WeatherTypes.CLOUDY,       1)
        );

        from(WeatherTypes.LIGHT_RAIN,
                to(WeatherTypes.DRIZZLE,      2),
                to(WeatherTypes.HEAVY_RAIN,   2),
                to(WeatherTypes.OVERCAST,     2),
                to(WeatherTypes.FREEZING_RAIN,1)
        );

        from(WeatherTypes.FREEZING_RAIN,
                to(WeatherTypes.LIGHT_RAIN,   2),
                to(WeatherTypes.SNOW,         2),
                to(WeatherTypes.HAIL,         1),
                to(WeatherTypes.OVERCAST,     1)
        );

        from(WeatherTypes.SNOW,
                to(WeatherTypes.OVERCAST,     2),
                to(WeatherTypes.BLIZZARD,     2),
                to(WeatherTypes.DRIZZLE,      1),
                to(WeatherTypes.FREEZING_RAIN,1)
        );

        from(WeatherTypes.BLIZZARD,
                to(WeatherTypes.SNOW,         3),
                to(WeatherTypes.OVERCAST,     1)
        );

        from(WeatherTypes.HAIL,
                to(WeatherTypes.HEAVY_RAIN,   2),
                to(WeatherTypes.THUNDERSTORM, 1),
                to(WeatherTypes.OVERCAST,     2)
        );

        from(WeatherTypes.DENSE_FOG,
                to(WeatherTypes.FOG,          3),
                to(WeatherTypes.OVERCAST,     2)
        );

        from(WeatherTypes.WINDY,
                to(WeatherTypes.SUNNY,        2),
                to(WeatherTypes.CLEAR,        1),
                to(WeatherTypes.CLOUDY,       2),
                to(WeatherTypes.SANDSTORM,    1),
                to(WeatherTypes.OVERCAST,     1)
        );

        from(WeatherTypes.SANDSTORM,
                to(WeatherTypes.WINDY,        3),
                to(WeatherTypes.CLEAR,        1)
        );


        // NETHER
        duration(WeatherTypes.NETHER_CLEAR,    18000, 36000);
        duration(WeatherTypes.ASH_STORM,       6000, 18000);
        duration(WeatherTypes.BRIMSTONE_STORM, 3000, 9000);
        duration(WeatherTypes.LAVA_RAIN,       2000, 6000);
        duration(WeatherTypes.NETHERSTORM,     2000, 5000);
        duration(WeatherTypes.HELLFIRE,        4000, 12000);

        from(WeatherTypes.NETHER_CLEAR,
                to(WeatherTypes.NETHER_CLEAR,    3),
                to(WeatherTypes.ASH_STORM,       3),
                to(WeatherTypes.BRIMSTONE_STORM, 1),
                to(WeatherTypes.LAVA_RAIN      , 1)
        );

        from(WeatherTypes.ASH_STORM,
                to(WeatherTypes.ASH_STORM,       3),
                to(WeatherTypes.BRIMSTONE_STORM, 2),
                to(WeatherTypes.HELLFIRE,        2),
                to(WeatherTypes.LAVA_RAIN,       1),
                to(WeatherTypes.NETHER_CLEAR,    1)
        );
        from(WeatherTypes.BRIMSTONE_STORM,
                to(WeatherTypes.ASH_STORM,       3),
                to(WeatherTypes.NETHERSTORM,     2),
                to(WeatherTypes.LAVA_RAIN,       1),
                to(WeatherTypes.NETHER_CLEAR,    1)
        );
        from(WeatherTypes.LAVA_RAIN,
                to(WeatherTypes.ASH_STORM,       3),
                to(WeatherTypes.BRIMSTONE_STORM, 2),
                to(WeatherTypes.HELLFIRE,        1),
                to(WeatherTypes.NETHER_CLEAR,    1)
        );
        from(WeatherTypes.NETHERSTORM,
                to(WeatherTypes.BRIMSTONE_STORM, 3),
                to(WeatherTypes.LAVA_RAIN,       2),
                to(WeatherTypes.ASH_STORM,       1),
                to(WeatherTypes.NETHER_CLEAR,    1)
        );
        from(WeatherTypes.HELLFIRE,
                to(WeatherTypes.ASH_STORM,       2),
                to(WeatherTypes.BRIMSTONE_STORM, 2),
                to(WeatherTypes.HELLFIRE,        1),
                to(WeatherTypes.NETHER_CLEAR,    1)
        );


        // END
        duration(WeatherTypes.END_CLEAR,   18000, 36000);
        duration(WeatherTypes.VOID_STORM,   4000, 12000);
        duration(WeatherTypes.END_MIST,     6000, 18000);
        duration(WeatherTypes.CHORUS_GALE,  3000, 9000);
        duration(WeatherTypes.ENDERSTORM,   2000, 6000);

        from(WeatherTypes.END_CLEAR,
                to(WeatherTypes.END_CLEAR,  4),
                to(WeatherTypes.END_MIST,   3),
                to(WeatherTypes.VOID_STORM, 1)
        );

        from(WeatherTypes.VOID_STORM,
                to(WeatherTypes.VOID_STORM,  2),
                to(WeatherTypes.END_MIST,    2),
                to(WeatherTypes.CHORUS_GALE, 2),
                to(WeatherTypes.ENDERSTORM,  1),
                to(WeatherTypes.END_CLEAR,   1)
        );
        from(WeatherTypes.END_MIST,
                to(WeatherTypes.VOID_STORM,  2),
                to(WeatherTypes.END_MIST,    2),
                to(WeatherTypes.CHORUS_GALE, 1),
                to(WeatherTypes.END_CLEAR,   1)
        );
        from(WeatherTypes.CHORUS_GALE,
                to(WeatherTypes.VOID_STORM,  2),
                to(WeatherTypes.END_MIST,    2),
                to(WeatherTypes.ENDERSTORM,  2),
                to(WeatherTypes.CHORUS_GALE, 1),
                to(WeatherTypes.END_CLEAR,   1)
        );
        from(WeatherTypes.ENDERSTORM,
                to(WeatherTypes.VOID_STORM,  2),
                to(WeatherTypes.CHORUS_GALE, 2),
                to(WeatherTypes.END_MIST,    1),
                to(WeatherTypes.END_CLEAR,   1)
        );
    }

    private static WeatherTypes defaultForDimension(WeatherTypes.Dimension dim) {
        return switch (dim) {
            case NETHER -> WeatherTypes.NETHER_CLEAR;
            case END    -> WeatherTypes.END_CLEAR;
            default     -> WeatherTypes.CLEAR;
        };
    }

    private static boolean isCompatible(WeatherTypes type, WeatherTypes.Dimension dim) {
        return type.dimension() == dim;
    }

    public static WeatherTypes nextWeather(WeatherTypes current, Random random,
                                           AtmosphericSystem atmosphere,
                                           WeatherTypes.Dimension dimension,
                                           ServerLevel level,
                                           List<WeatherTypes> recentHistory) {
        List<Transition> transitions = TRANSITIONS.getOrDefault(current, List.of())
                .stream()
                .filter(t -> isCompatible(t.target(), dimension))
                .filter(t -> isEnabled(t.target()))
                .toList();

        if (transitions.isEmpty()) return defaultForDimension(dimension);

        float[] weights = new float[transitions.size()];
        float totalWeight = 0f;

        for (int i = 0; i < transitions.size(); i++) {
            Transition t = transitions.get(i);
            float w = t.weight();

            if (dimension == WeatherTypes.Dimension.OVERWORLD) {
                float p        = atmosphere.getNormalized(level);
                float tempC    = atmosphere.getTemperature();
                float humidity = atmosphere.getHumidity();
                w *= AtmosphereModifiers.pressureModifier(t.target(), p);
                w *= AtmosphereModifiers.atmosphereModifier(t.target(), tempC, humidity);
                w *= SeasonModifiers.seasonModifier(t.target(), level);
                w *= AtmosphericForcing.biasModifier(t.target(), atmosphere.dominantBias());
            }

            w *= WeatherHistoryModifiers.historyModifier(t.target(), recentHistory);

            weights[i]   = Math.max(0.001f, w);
            totalWeight += weights[i];
        }

        float roll = random.nextFloat() * totalWeight;
        float cumulative = 0f;
        for (int i = 0; i < transitions.size(); i++) {
            cumulative += weights[i];
            if (roll < cumulative) return transitions.get(i).target();
        }
        return defaultForDimension(dimension);
    }

    public record Prediction(WeatherTypes type, List<Transition> transitions, float[] weights) {}

    public static Prediction mostLikelyNextWithWeights(WeatherTypes current, AtmosphericSystem atmosphere,
                                                       WeatherTypes.Dimension dimension,
                                                       ServerLevel level,
                                                       List<WeatherTypes> recentHistory) {
        List<Transition> transitions = TRANSITIONS.getOrDefault(current, List.of())
                .stream()
                .filter(t -> isCompatible(t.target(), dimension))
                .filter(t -> isEnabled(t.target()))
                .toList();

        if (transitions.isEmpty()) {
            WeatherTypes fallback = defaultForDimension(dimension);
            return new Prediction(fallback, List.of(), new float[0]);
        }

        float p        = atmosphere.getNormalized(level);
        float tempC    = atmosphere.getTemperature();
        float humidity = atmosphere.getHumidity();

        float[] weights = new float[transitions.size()];
        WeatherTypes best = null;
        float bestWeight = -1f;

        for (int i = 0; i < transitions.size(); i++) {
            Transition t = transitions.get(i);
            float w = t.weight();

            if (dimension == WeatherTypes.Dimension.OVERWORLD) {
                w *= AtmosphereModifiers.pressureModifier(t.target(), p);
                w *= AtmosphereModifiers.atmosphereModifier(t.target(), tempC, humidity);
                w *= SeasonModifiers.seasonModifier(t.target(), level);
                w *= AtmosphericForcing.biasModifier(t.target(), atmosphere.dominantBias());
            }

            w *= WeatherHistoryModifiers.historyModifier(t.target(), recentHistory);
            weights[i] = w;
            weights[i] = Math.max(0.001f, w);

            if (w > bestWeight) {
                bestWeight = w;
                best = t.target();
            }
        }

        return new Prediction(best != null ? best : defaultForDimension(dimension), transitions, weights);
    }

    public static Prediction mostLikelyNextFromMeasurements(WeatherTypes from,
                                                            float normalizedPressure, float tempC, float humidity,
                                                            WeatherTypes.Dimension dimension, ServerLevel level,
                                                            List<WeatherTypes> measuredHistory) {

        List<Transition> transitions = TRANSITIONS.getOrDefault(from, List.of())
                .stream()
                .filter(t -> isCompatible(t.target(), dimension))
                .filter(t -> isEnabled(t.target()))
                .toList();

        if (transitions.isEmpty()) {
            WeatherTypes fallback = defaultForDimension(dimension);
            return new Prediction(fallback, List.of(), new float[0]);
        }

        float[] weights = new float[transitions.size()];
        WeatherTypes best = null;
        float bestWeight = -1f;

        for (int i = 0; i < transitions.size(); i++) {
            Transition t = transitions.get(i);
            float w = t.weight();

            if (dimension == WeatherTypes.Dimension.OVERWORLD) {
                w *= AtmosphereModifiers.pressureModifier(t.target(), normalizedPressure);
                w *= AtmosphereModifiers.atmosphereModifier(t.target(), tempC, humidity);
                w *= SeasonModifiers.seasonModifier(t.target(), level);
            }

            w *= WeatherHistoryModifiers.historyModifier(t.target(), measuredHistory);
            weights[i] = w;
            weights[i] = Math.max(0.001f, w);

            if (w > bestWeight) { bestWeight = w; best = t.target(); }
        }

        return new Prediction(best != null ? best : defaultForDimension(dimension), transitions, weights);
    }

    public static List<TransitionProbabilitiesPacket.Entry> computeProbabilities(
            WeatherTypes current, AtmosphericSystem atmosphere,
            WeatherTypes.Dimension dimension, ServerLevel level,
            List<WeatherTypes> recentHistory) {

        Prediction prediction = mostLikelyNextWithWeights(current, atmosphere, dimension, level, recentHistory);

        if (prediction.transitions().isEmpty()) return List.of();

        float total = 0f;
        for (float w : prediction.weights()) total += w;
        if (total <= 0f) return List.of();

        List<TransitionProbabilitiesPacket.Entry> entries = new java.util.ArrayList<>();
        for (int i = 0; i < prediction.transitions().size(); i++) {
            WeatherTypes target = prediction.transitions().get(i).target();
            float percent = (prediction.weights()[i] / total) * 100f;
            entries.add(new TransitionProbabilitiesPacket.Entry(target, percent));
        }

        entries.sort((a, b) -> Float.compare(b.probabilityPercent(), a.probabilityPercent()));
        return entries;
    }

    public record RankedForecast(WeatherTypes type, float probability) {}

    public static List<RankedForecast> topCandidates(Prediction prediction, int n) {
        if (prediction.transitions().isEmpty()) return List.of();

        float total = 0f;
        for (float w : prediction.weights()) total += w;
        if (total <= 0f) return List.of();

        List<RankedForecast> ranked = new ArrayList<>();
        for (int i = 0; i < prediction.transitions().size(); i++) {
            ranked.add(new RankedForecast(
                    prediction.transitions().get(i).target(),
                    prediction.weights()[i] / total));
        }
        ranked.sort((a, b) -> Float.compare(b.probability(), a.probability()));
        return ranked.subList(0, Math.min(n, ranked.size()));
    }

    public static int randomDuration(WeatherTypes type, Random random, long timeOfDay, ServerLevel level) {
        int min = MIN_DURATION.getOrDefault(type, 6000);
        int max = MAX_DURATION.getOrDefault(type, 12000);

        float hour = (timeOfDay + 6000) % 24000 / 1000f;

        float factor = switch (type) {

            case FOG, DENSE_FOG -> hour < 8f  ? 1.8f
                    : hour < 12f ? 1.0f
                    : hour < 17f ? 0.3f
                    : 0.7f;

            case MIST -> hour < 9f  ? 1.5f
                    : hour < 13f ? 1.0f
                    : hour < 18f ? 0.4f
                    : 0.8f;

            case THUNDERSTORM -> hour < 6f  ? 0.5f
                    : hour < 12f ? 0.8f
                    : hour < 19f ? 1.5f
                    : 0.7f;

            case FREEZING_RAIN -> hour < 8f  ? 1.5f
                    : hour < 11f ? 0.8f
                    : 0.5f;

            case HAIL -> hour < 12f ? 0.6f
                    : hour < 20f ? 1.4f
                    : 0.8f;

            case SNOW -> hour < 6f  ? 1.4f
                    : hour < 14f ? 1.0f
                    : hour < 20f ? 0.8f
                    : 1.2f;

            case BLIZZARD -> 1.0f;

            case CLEAR, SUNNY -> hour < 6f  ? 0.6f
                    : hour < 8f  ? 1.0f
                    : hour < 19f ? 1.4f
                    : 0.8f;

            case WINDY -> hour < 10f ? 0.7f
                    : hour < 18f ? 1.3f
                    : 0.9f;

            case SANDSTORM -> hour < 10f ? 0.5f
                    : hour < 17f ? 1.6f
                    : 0.6f;

            default -> 1.0f;
        };

        float seasonFactor = SeasonModifiers.durationModifier(type, level);
        factor *= seasonFactor;

        min = (int)(min * factor);
        max = (int)(max * factor);
        return min + random.nextInt(Math.max(1, max - min));
    }

    private static boolean isEnabled(WeatherTypes type) {
        AWCommonConfig config = AWCommonConfig.get();
        return switch (type) {
            case FOG           -> config.enableFog;
            case DENSE_FOG     -> config.enableDenseFog;
            case MIST          -> config.enableMist;
            case SANDSTORM     -> config.enableSandstorm;
            case BLIZZARD      -> config.enableBlizzard;
            case FREEZING_RAIN -> config.enableFreezingRain;
            case HAIL          -> config.enableHail;
            case THUNDERSTORM  -> config.enableThunderstorm;

            case ASH_STORM        -> config.enableNetherWeather && config.enableAshStorm;
            case BRIMSTONE_STORM  -> config.enableNetherWeather && config.enableBrimstoneStorm;
            case LAVA_RAIN        -> config.enableNetherWeather && config.enableLavaRain;
            case NETHERSTORM      -> config.enableNetherWeather && config.enableNetherstorm;
            case HELLFIRE         -> config.enableNetherWeather && config.enableHellfire;

            case VOID_STORM    -> config.enableEndWeather && config.enableVoidStorm;
            case END_MIST      -> config.enableEndWeather && config.enableEndMist;
            case CHORUS_GALE   -> config.enableEndWeather && config.enableChorusGale;
            case ENDERSTORM    -> config.enableEndWeather && config.enableEnderstorm;

            default -> true;
        };
    }

    public static int getMinDuration(WeatherTypes type) {
        return MIN_DURATION.getOrDefault(type, 6000);
    }

    public static int getMaxDuration(WeatherTypes type) {
        return MAX_DURATION.getOrDefault(type, 12000);
    }

    public static List<Transition> getTransitions(WeatherTypes from) {
        return TRANSITIONS.getOrDefault(from, List.of());
    }


    private static void duration(WeatherTypes type, int min, int max) {
        MIN_DURATION.put(type, min);
        MAX_DURATION.put(type, max);
    }

    private static void from(WeatherTypes type, Transition... transitions) {
        TRANSITIONS.put(type, List.of(transitions));
    }

    private static Transition to(WeatherTypes target, float weight) {
        return new Transition(target, weight);
    }
}
