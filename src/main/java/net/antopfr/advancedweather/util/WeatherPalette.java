package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.world.phys.Vec3;

public class WeatherPalette {

    public static Vec3 tint(WeatherTypes t) {
        return switch (t) {
            case THUNDERSTORM       -> new Vec3(0.42, 0.44, 0.50);
            case HEAVY_RAIN         -> new Vec3(0.55, 0.57, 0.61);
            case BLIZZARD           -> new Vec3(0.80, 0.83, 0.88);
            case OVERCAST           -> new Vec3(0.64, 0.65, 0.68);
            case FREEZING_RAIN      -> new Vec3(0.70, 0.72, 0.76);
            case HAIL               -> new Vec3(0.62, 0.64, 0.68);
            case LIGHT_RAIN         -> new Vec3(0.70, 0.71, 0.74);
            case SNOW               -> new Vec3(0.82, 0.84, 0.88);
            case DRIZZLE            -> new Vec3(0.76, 0.77, 0.80);
            case FOG, DENSE_FOG     -> new Vec3(0.80, 0.80, 0.82);
            case MIST               -> new Vec3(0.86, 0.86, 0.88);
            case CLOUDY             -> new Vec3(0.88, 0.89, 0.91);
            case SANDSTORM          -> new Vec3(0.82, 0.70, 0.50);
            case WINDY              -> new Vec3(0.95, 0.96, 0.97);
            default                 -> new Vec3(1.0, 1.0, 1.0);
        };
    }

    public static float skyDesaturation(WeatherTypes t) {
        return switch (t) {
            case THUNDERSTORM, BLIZZARD             -> 0.90f;
            case OVERCAST, HEAVY_RAIN               -> 0.85f;
            case FREEZING_RAIN, HAIL, SNOW          -> 0.70f;
            case LIGHT_RAIN, DRIZZLE                -> 0.55f;
            case FOG, DENSE_FOG                     -> 0.60f;
            case MIST, CLOUDY                       -> 0.25f;
            default                                 -> 0f;
        };
    }

    public static float ambient() {
        Vec3 sky = SkyMixinContext.vanillaSkyColor();
        return (float) Math.max(0.06, (sky.x + sky.y + sky.z) / 3.0);
    }
}
