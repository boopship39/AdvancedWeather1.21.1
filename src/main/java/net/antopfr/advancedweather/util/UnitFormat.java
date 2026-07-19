package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.config.AWClientConfig;

public class UnitFormat {
    public static float temperatureValue(float celsius) {
        return AWClientConfig.get().useFahrenheit ? celsius * 1.8f + 32f : celsius;
    }

    public static String temperatureUnit() {
        return AWClientConfig.get().useFahrenheit ? "°F" : "°C";
    }

    public static String temperature(float celsius) {
        return String.format("%.1f %s", temperatureValue(celsius), temperatureUnit());
    }

    public static float windValue(float kmh) {
        return AWClientConfig.get().useMph ? kmh * 0.621371f : kmh;
    }

    public static String windUnit() {
        return AWClientConfig.get().useMph ? "mph" : "km/h";
    }

    public static String wind(float kmh) {
        return String.format("%.1f %s", windValue(kmh), windUnit());
    }
}
