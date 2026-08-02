package net.antopfr.advancedweather.content.item.crystals;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.weather.AtmosphericForcing;
import net.minecraft.util.Mth;

public class CrystalFavor {

    public static float favor(AtmosphericForcing.Bias bias) {

        float pressure = ClientAtmosphereState.getLocalPressure();
        float temp = ClientAtmosphereState.getLocalTemperature();
        float humidity = ClientAtmosphereState.getLocalHumidity();

        return switch (bias) {
            case SEEDING -> Mth.clamp((1020f - pressure) / 60f, 0f, 1f)
                    * Mth.clamp(humidity / 100f, 0f, 1f);

            case DISSIPATING -> Mth.clamp((pressure - 1010f) / 40f, 0f, 1f)
                    * Mth.clamp(1f - humidity / 100f, 0f, 1f);

            case COOLING -> Mth.clamp((8f - temp) / 20f, 0f, 1f);

            case HEATING -> Mth.clamp((temp - 15f) / 25f, 0f, 1f);

            case NONE -> 0.3f;
        };
    }
}
