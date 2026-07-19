/**
 * Public, stable API of Advanced Weather.
 *
 * <p>Types in this package (and {@code net.antopfr.advancedweather.api.event}) are the only
 * ones other mods should depend on. The {@link net.antopfr.advancedweather.weather.WeatherTypes}
 * enum is also considered part of the API surface. Everything else in the mod is internal and
 * may change without notice.
 *
 * <ul>
 *   <li>{@link net.antopfr.advancedweather.api.AdvancedWeatherAPI} - server-side reads and control.</li>
 *   <li>{@link net.antopfr.advancedweather.api.AdvancedWeatherClientAPI} - client-side reads (client only).</li>
 *   <li>{@link net.antopfr.advancedweather.api.event.WeatherChangeEvent} - fired when weather changes.</li>
 *   <li>{@link net.antopfr.advancedweather.api.AtmosphereSnapshot},
 *       {@link net.antopfr.advancedweather.api.WeatherForecast} - immutable data returned by the API.</li>
 * </ul>
 *
 * <p>Note: {@code net.antopfr.advancedweather.api.external} holds the mod's own HTTP clients for
 * Open-Meteo and Nominatim. Those are internal helpers, not part of this public API.
 */
package net.antopfr.advancedweather.api;
