package net.antopfr.advancedweather.api;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;

/**
 * Public, stable client-side reads of what the local player currently perceives:
 * the synced weather plus the smoothed atmospheric values used by the HUD.
 *
 * <p><b>Client only.</b> Every method reads client display state and must be called on the
 * client (render/client thread). Do not reference this class from common or server code —
 * for server-authoritative reads use {@link AdvancedWeatherAPI}.
 *
 * <p>Values here are the <i>client's</i> smoothed/interpolated copies (what the player sees),
 * which may differ slightly from the authoritative server state mid-transition.
 */
public final class AdvancedWeatherClientAPI {

    private AdvancedWeatherClientAPI() {}

    /** Weather currently shown to the local player. */
    public static WeatherTypes getCurrentWeather() {
        return ClientWeatherState.getCurrentWeather();
    }

    /** Weather the client is transitioning away from. */
    public static WeatherTypes getPreviousWeather() {
        return ClientWeatherState.getPreviousWeather();
    }

    /** Transition progress between previous and current weather, 0-1. */
    public static float getTransitionProgress() {
        return ClientWeatherState.getTransitionProgress();
    }

    /** Client air pressure in hPa. */
    public static float getPressure() {
        return ClientAtmosphereState.getPressure();
    }

    /** Per-tick pressure trend; positive = rising, negative = falling. */
    public static float getPressureTrend() {
        return ClientAtmosphereState.getTrend();
    }

    /** Pressure band name: {@code "HIGH"}, {@code "NEUTRAL"}, {@code "LOW"} or {@code "STORM"}. */
    public static String getPressureCategory() {
        return ClientAtmosphereState.getCategory();
    }

    /** Normalized wind intensity, 0 (calm) to 1 (storm-force). */
    public static float getWindIntensity() {
        return ClientAtmosphereState.getWindIntensity();
    }

    /** Local air temperature in °C (includes the player's local biome offset). */
    public static float getTemperature() {
        return ClientAtmosphereState.getLocalTemperature();
    }

    /** Local relative humidity in percent (0-100). */
    public static float getHumidity() {
        return ClientAtmosphereState.getLocalHumidity();
    }

    /** Display name of the predicted next weather. */
    public static String getPredictedNext() {
        return ClientAtmosphereState.getPredictedNext();
    }

    /** Display name of the predicted weather 30 in-game minutes out. */
    public static String getPredictedIn30Min() {
        return ClientAtmosphereState.getPredictedIn30min();
    }

    /** Driving mode name: {@code "PROCEDURAL"}, {@code "REAL"} or {@code "MANUAL"}. */
    public static String getMode() {
        return ClientAtmosphereState.getMode();
    }
}
