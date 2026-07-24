package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.util.Key;

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
        if (feelsLike < -65) return Key.t("advancedweather.feelslike.-65");
        if (feelsLike < -45) return Key.t("advancedweather.feelslike.-45");
        if (feelsLike < -25) return Key.t("advancedweather.feelslike.-25");
        if (feelsLike < -10) return Key.t("advancedweather.feelslike.-10");
        if (feelsLike <   0) return Key.t("advancedweather.feelslike.0");
        if (feelsLike <   8) return Key.t("advancedweather.feelslike.8");
        if (feelsLike <  15) return Key.t("advancedweather.feelslike.15");
        if (feelsLike <  20) return Key.t("advancedweather.feelslike.20");
        if (feelsLike <  26) return Key.t("advancedweather.feelslike.26");
        if (feelsLike <  32) return Key.t("advancedweather.feelslike.32");
        if (feelsLike <  42) return Key.t("advancedweather.feelslike.42");
        if (feelsLike <  65) return Key.t("advancedweather.feelslike.65");
        if (feelsLike <  85) return Key.t("advancedweather.feelslike.85");
        if (feelsLike <  125) return Key.t("advancedweather.feelslike.125");
        return Key.t("advancedweather.feelslike.max");
    }
}