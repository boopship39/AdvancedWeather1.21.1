package net.antopfr.advancedweather.api;

import net.antopfr.advancedweather.weather.*;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

/**
 * Public, stable entry point for reading and controlling Advanced Weather from other mods.
 *
 * <p><b>Threading:</b> every method here reads or mutates authoritative server state and
 * must be called on the server thread (e.g. from a server tick, command, block entity or a
 * {@code server.execute(...)} block). Do not call these from a render/client thread.
 *
 * <p><b>Dimensions:</b> weather is tracked per {@link WeatherTypes.Dimension} (Overworld,
 * Nether, End). Each method resolves the group from the {@link ServerLevel} you pass.
 *
 * @see net.antopfr.advancedweather.api.event.WeatherChangeEvent
 * @see AdvancedWeatherClientAPI for client-side reads
 */
public final class AdvancedWeatherAPI {

    private AdvancedWeatherAPI() {}

    /** The weather currently active in the given level. */
    public static WeatherTypes getCurrentWeather(ServerLevel level) {
        return WeatherManager.get(level).getCurrentWeather(level);
    }

    /** An immutable snapshot of the level's atmospheric state (pressure, temperature, ...). */
    public static AtmosphereSnapshot getAtmosphere(ServerLevel level) {
        AtmosphericSystem a = WeatherManager.get(level).getAtmosphere(level);
        return new AtmosphereSnapshot(
                a.getPressure(), a.getPressureVel(), a.getDewPoint(),
                a.getTemperature(), a.getHumidity(), a.getWindIntensity(),
                a.getMode().name(), a.getCategory(level).name());
    }

    /** The current forecast (next weather + weather in 30 in-game minutes, each with a confidence). */
    public static WeatherForecast getForecast(ServerLevel level) {
        WeatherManager manager = WeatherManager.get(level);
        AtmosphericSystem atmosphere = manager.getAtmosphere(level);
        WeatherTypes current = manager.getCurrentWeather(level);
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        List<WeatherTypes> history = manager.getRecentHistory(level);
        long time = level.getGameTime();

        AtmosphericSystem forecastState = atmosphere.forecastState(30, time, level);

        WeatherTransitionGraph.Prediction next =
                WeatherTransitionGraph.mostLikelyNextWithWeights(current, atmosphere, dim, level, history);
        WeatherTransitionGraph.Prediction in30 =
                WeatherTransitionGraph.mostLikelyNextWithWeights(current, forecastState, dim, level, history);

        float confidenceNext = ForecastConfidence.compute(next.transitions(), next.weights(), atmosphere, 0);
        float confidenceIn30 = ForecastConfidence.compute(in30.transitions(), in30.weights(), forecastState, 30);

        return new WeatherForecast(next.type(), confidenceNext, in30.type(), confidenceIn30);
    }

    /** The recent weather history for the level, oldest first. */
    public static List<WeatherTypes> getRecentHistory(ServerLevel level) {
        return WeatherManager.get(level).getRecentHistory(level);
    }

    /** Whether the current weather maps to vanilla rain. */
    public static boolean isRaining(ServerLevel level) {
        return getCurrentWeather(level).isVanillaRaining();
    }

    /** Whether the current weather maps to vanilla thunder. */
    public static boolean isThundering(ServerLevel level) {
        return getCurrentWeather(level).isVanillaThundering();
    }

    /**
     * Sets the weather for the level and pins it (manual mode) until changed again.
     *
     * @return {@code false} if the type is not valid for the level's dimension (nothing changed),
     *         {@code true} otherwise
     */
    public static boolean setWeather(ServerLevel level, WeatherTypes type) {
        if (!WeatherManager.isCompatibleWithLevel(type, level)) return false;
        WeatherManager.get(level).setCurrentWeather(level, type);
        return true;
    }

    /**
     * Hands control back to the procedural (or real-weather, if enabled) engine, so the
     * weather resumes evolving on its own.
     */
    public static void startAutoWeather(ServerLevel level) {
        WeatherManager.get(level).startAutoWeather(level);
    }

    /**
     * Nudges the atmosphere: applies a temporary forcing that pushes pressure, dew point and
     * temperature toward the given deltas, decaying over {@code durationTicks}. Useful for
     * events, rituals or machines that should influence the weather without hard-setting it.
     *
     * @param pressurePush  total pressure delta to push toward (hPa)
     * @param dewPointPush  total dew-point delta to push toward (°C)
     * @param tempOffset    temperature offset while active (°C)
     * @param durationTicks how long the forcing lasts, in ticks
     */
    public static void applyForcing(ServerLevel level, float pressurePush, float dewPointPush,
                                    float tempOffset, int durationTicks, AtmosphericForcing.Bias bias) {
        WeatherManager.get(level).getAtmosphere(level)
                .applyForcing(pressurePush, dewPointPush, tempOffset, durationTicks, bias);
    }
}
