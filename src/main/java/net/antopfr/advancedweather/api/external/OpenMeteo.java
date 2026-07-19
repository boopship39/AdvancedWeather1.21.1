package net.antopfr.advancedweather.api.external;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.antopfr.advancedweather.util.AWLogger;
import net.antopfr.advancedweather.weather.WeatherTypes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class OpenMeteo {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final String URL_TEMPLATE =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=%s&longitude=%s" +
                    "&current=weather_code,wind_speed_10m,is_day" +
                    "&timezone=auto&wind_speed_unit=ms&forecast_days=1";

    public static CompletableFuture<WeatherTypes> fetchAsync(double lat, double lon) {
        String url = String.format(URL_TEMPLATE,
                String.valueOf(lat).replace(',', '.'),
                String.valueOf(lon).replace(',', '.'));

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .timeout(Duration.ofSeconds(8))
                                .GET()
                                .build();

                        HttpResponse<String> response = HTTP.send(
                                request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() != 200) {
                            AWLogger.AW.warn(
                                    "[AdvancedWeather] Open-Meteo returned HTTP {}",
                                    response.statusCode());
                            return null;
                        }

                        return parseWeatherCode(response.body());

                    } catch (Exception e) {
                        AWLogger.AW.warn(
                                "[AdvancedWeather] Open-Meteo unreachable: {}", e.getMessage());
                        return null;
                    }
                });
    }

    private static WeatherTypes parseWeatherCode(String json) {
        try {
            JsonObject root    = JsonParser.parseString(json).getAsJsonObject();
            JsonObject current = root.getAsJsonObject("current");
            int code           = current.get("weather_code").getAsInt();
            float wind         = current.get("wind_speed_10m").getAsFloat();
            return wmoToWeatherType(code, wind);
        } catch (Exception e) {
            AWLogger.AW.warn("[AdvancedWeather] Failed to parse Open-Meteo response");
            return null;
        }
    }

    private static WeatherTypes wmoToWeatherType(int code, float windMs) {
        float windNormalized = Math.clamp(windMs / 25f, 0f, 1f);

        return switch (code) {
            case 0             -> WeatherTypes.CLEAR;
            case 1             -> WeatherTypes.SUNNY;
            case 2             -> WeatherTypes.CLOUDY;
            case 3             -> WeatherTypes.OVERCAST;
            case 10            -> WeatherTypes.MIST;
            case 45            -> windMs < 1.5f ? WeatherTypes.MIST : WeatherTypes.FOG;
            case 48            -> WeatherTypes.DENSE_FOG;
            case 51, 53        -> WeatherTypes.DRIZZLE;
            case 55            -> WeatherTypes.LIGHT_RAIN;
            case 61, 63        -> WeatherTypes.LIGHT_RAIN;
            case 65            -> WeatherTypes.HEAVY_RAIN;
            case 66, 67        -> WeatherTypes.FREEZING_RAIN;
            case 71, 73        -> WeatherTypes.SNOW;
            case 75, 77        -> code == 77    ? WeatherTypes.HAIL  : WeatherTypes.BLIZZARD;
            case 80, 81        -> WeatherTypes.LIGHT_RAIN;
            case 82            -> WeatherTypes.HEAVY_RAIN;
            case 85, 86        -> WeatherTypes.SNOW;
            case 95            -> WeatherTypes.THUNDERSTORM;
            case 96, 99        -> WeatherTypes.THUNDERSTORM;
            default            -> WeatherTypes.CLEAR;
        };
    }
}
