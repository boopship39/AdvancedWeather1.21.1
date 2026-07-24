package net.antopfr.advancedweather.weather.effect.global.wind;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.util.Key;

public class WindSpeedCalculation {
    public static final float MAX_WIND_KMH = 120.0f;

    public static float getWindSpeed() {
        float w = ClientAtmosphereState.getWindIntensity();
        return w * w * MAX_WIND_KMH;
    }

    public static String getBeaufortLabel(float kmh) {
        if (kmh < 1)   return Key.t("advancedweather.wind.calm");
        if (kmh < 6)   return Key.t("advancedweather.wind.light_air");
        if (kmh < 12)  return Key.t("advancedweather.wind.light_breeze");
        if (kmh < 20)  return Key.t("advancedweather.wind.gentle_breeze");
        if (kmh < 29)  return Key.t("advancedweather.wind.moderate_breeze");
        if (kmh < 39)  return Key.t("advancedweather.wind.fresh_breeze");
        if (kmh < 50)  return Key.t("advancedweather.wind.strong_breeze");
        if (kmh < 62)  return Key.t("advancedweather.wind.high_wind_near_gale");
        if (kmh < 75)  return Key.t("advancedweather.wind.gale");
        if (kmh < 89)  return Key.t("advancedweather.wind.strong_gale");
        if (kmh < 103) return Key.t("advancedweather.wind.storm");
        if (kmh < 118) return Key.t("advancedweather.wind.violent_storm");
        return Key.t("advancedweather.wind.hurricane");
    }
}
