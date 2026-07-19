package net.antopfr.advancedweather.api;

import net.antopfr.advancedweather.weather.WeatherTypes;

/**
 * Immutable weather forecast for a dimension, derived from the current atmospheric
 * state and the weighted transition graph. Part of the public Advanced Weather API.
 *
 * @param predictedNext      most likely weather to follow the current one
 * @param confidenceNext     confidence in {@code predictedNext}, 0-100
 * @param predictedIn30Min   most likely weather 30 in-game minutes from now
 * @param confidenceIn30Min  confidence in {@code predictedIn30Min}, 0-100
 */
public record WeatherForecast(
        WeatherTypes predictedNext,
        float confidenceNext,
        WeatherTypes predictedIn30Min,
        float confidenceIn30Min
) {}
