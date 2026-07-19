package net.antopfr.advancedweather.weather.effect.global.wind;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;

public class WindSpeedCalculation {
    public static final float MAX_WIND_KMH = 120.0f;

    /**
     * Calcule la vitesse réelle du vent en km/h (loi quadratique sur la puissance de l'air)
     */
    public static float getWindSpeed() {
        float w = ClientAtmosphereState.getWindIntensity();
        return w * w * MAX_WIND_KMH;
    }

    /**
     * Échelle de Beaufort officielle pour l'affichage de ta station météo
     */
    public static String getBeaufortLabel(float kmh) {
        if (kmh < 1)   return "Calm";
        if (kmh < 6)   return "Light air";
        if (kmh < 12)  return "Light breeze";
        if (kmh < 20)  return "Gentle breeze";
        if (kmh < 29)  return "Moderate breeze";
        if (kmh < 39)  return "Fresh breeze";
        if (kmh < 50)  return "Strong breeze";
        if (kmh < 62)  return "High wind (Near gale)";
        if (kmh < 75)  return "Gale";
        if (kmh < 89)  return "Strong gale";
        if (kmh < 103) return "Storm";
        if (kmh < 118) return "Violent storm";
        return "Hurricane";
    }
}
