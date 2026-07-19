package net.antopfr.advancedweather.weather;

public class FeelsLikeCalculation {
    public static float calculate(float tempC, float windKmh, float humidityPercent) {
        float result = tempC;

        if (windKmh >= 4.8f) {
            float windChill = (float)(
                    13.12 + 0.6215  * tempC - 11.37   * Math.pow(windKmh, 0.16) + 0.3965  * tempC * Math.pow(windKmh, 0.16)
            );
            float blend = Math.clamp((20f - tempC) / 20f, 0f, 1f);
            result += (windChill - tempC) * blend;
        }

        if (tempC >= 20f) {
            float humidityEffect = (humidityPercent - 50f) / 50f * 5.5f;
            float blend = Math.clamp((tempC - 20f) / 15f, 0f, 1f);
            result += humidityEffect * blend;
        } else {
            float humidityEffect = -(humidityPercent / 100f) * 4f;
            float blend = Math.clamp((20f - tempC) / 20f, 0f, 1f);
            result += humidityEffect * blend;
        }

        if (tempC >= 30f && windKmh >= 10f) {
            float hotWindEffect = windKmh * 0.10f;
            float blend = Math.clamp((tempC - 30f) / 15f, 0f, 1f);
            result += hotWindEffect * blend;
        }

        return Math.clamp(result, -100f, 150f);
    }

    public static String getComfortLabel(float feelsLike) {
        if (feelsLike < -65) return "Just like outer-space";
        if (feelsLike < -45) return "Cosmic frost";
        if (feelsLike < -25) return "Abyssal cold";
        if (feelsLike < -10) return "Very cold";
        if (feelsLike <   0) return "Freezing";
        if (feelsLike <   8) return "Cold";
        if (feelsLike <  15) return "Cool";
        if (feelsLike <  20) return "Comfortable";
        if (feelsLike <  26) return "Warm";
        if (feelsLike <  32) return "Hot";
        if (feelsLike <  42) return "Very hot";
        if (feelsLike <  65) return "Unbearable heat";
        if (feelsLike <  85) return "Incinerating heat";
        if (feelsLike <  125) return "Vaporizing heat";
        return "Just like an oven";
    }
}