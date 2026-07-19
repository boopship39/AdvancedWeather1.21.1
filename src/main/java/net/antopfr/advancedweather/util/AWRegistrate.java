package net.antopfr.advancedweather.util;

import com.tterrag.registrate.Registrate;
import net.antopfr.advancedweather.AdvancedWeather;

public class AWRegistrate {
    private static Registrate REGISTRATE;

    public static Registrate get() {
        if (REGISTRATE == null) {
            REGISTRATE = Registrate.create(AdvancedWeather.MOD_ID);
            REGISTRATE.defaultCreativeTab("advancedweather");
        }
        return REGISTRATE;
    }
}
