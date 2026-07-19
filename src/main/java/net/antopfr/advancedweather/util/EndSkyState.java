package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class EndSkyState {
    private static float r = 1f, g = 1f, b = 1f;
    private static float tr = 1f, tg = 1f, tb = 1f;
    private static int retargetCooldown = 0;
    private static final RandomSource random = RandomSource.create();

    private static float pulsePhase = 0f;

    public static void tick() {
        boolean voidStorm = ClientWeatherState.getCurrentWeather() == WeatherTypes.VOID_STORM;

        if (voidStorm) {
            if (retargetCooldown <= 0) {
                retargetCooldown = 40 + random.nextInt(60);
                tr = 0.7f + random.nextFloat() * 0.9f;
                tg = 0.3f + random.nextFloat() * 0.4f;
                tb = 1.0f + random.nextFloat();
            }
            retargetCooldown--;
            pulsePhase += 0.08f;
        } else {
            tr = 1f; tg = 1f; tb = 1f;
        }

        r = Mth.lerp(0.03f, r, tr);
        g = Mth.lerp(0.03f, g, tg);
        b = Mth.lerp(0.03f, b, tb);
    }

    private static float pulse() {
        if (ClientWeatherState.getCurrentWeather() != WeatherTypes.VOID_STORM) return 1f;
        float slow = (float) Math.sin(pulsePhase) * 0.18f;
        float fast = (float) Math.sin(pulsePhase * 2.7f) * 0.08f;
        return 1f + slow + fast;
    }
    public static float red()   { return r * pulse(); }
    public static float green() { return g * pulse(); }
    public static float blue()  { return b * pulse(); }
}