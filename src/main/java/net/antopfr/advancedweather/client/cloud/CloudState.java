package net.antopfr.advancedweather.client.cloud;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.util.WeatherPalette;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirection;
import net.antopfr.advancedweather.weather.maps.BiomeFogColors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CloudState {
    public static final float CELL_SIZE = 8f;
    public static final float CELL_HEIGHT = 6f;

    private static final float BIOME_INFLUENCE = 0.35f;

    private static final float THRESHOLD_STEP = 0.004f;

    private static float coverage = 0f;
    private static float altitude = 160f;
    private static double scrollX = 0, scrollZ = 0;

    private static float opacity = 0.6f;

    private static float mixedR = 1f, mixedG = 1f, mixedB = 1f;

    private static float sunAngle = 0f;
    private static Vec3 sunDir = new Vec3(0, 1, 0);
    private static float goldenHour = 0f;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        WeatherTypes weather = ClientWeatherState.getCurrentWeather();

        float base = baseCoverage(weather);
        float margin = coverageMargin(weather);
        float humidityNorm = Mth.clamp((ClientAtmosphereState.getLocalHumidity() - 60f) / 40f, -1f, 1f);
        float target = Mth.clamp(base + humidityNorm * margin, 0f, 1f);

        coverage = Mth.lerp(0.004f, coverage, target);
        altitude = Mth.lerp(0.004f, altitude, altitudeFor(weather));

        Vec3 typeTint = WeatherPalette.tint(weather);

        BlockPos pos = mc.player != null ? mc.player.blockPosition() : BlockPos.ZERO;
        ResourceLocation biome = mc.level.getBiome(pos).unwrapKey()
                .map(ResourceKey::location).orElse(null);

        Vec3 targetColor = typeTint;
        if (weather.hasFog()) {
            int fog = BiomeFogColors.getColor(weather, biome);
            Vec3 fogVec = normalizedHue(fog);
            targetColor = typeTint.lerp(fogVec, BIOME_INFLUENCE);
        }

        mixedR = Mth.lerp(0.004f, mixedR, (float) targetColor.x);
        mixedG = Mth.lerp(0.004f, mixedG, (float) targetColor.y);
        mixedB = Mth.lerp(0.004f, mixedB, (float) targetColor.z);
        opacity = Mth.lerp(0.004f, opacity, opacityFor(weather));

        float raw = mc.level.getSunAngle(0f);
        sunAngle = Math.round(raw / 0.05f) * 0.05f;

        double a = mc.level.getSunAngle(0f) * Math.PI * 2.0;
        sunDir = new Vec3(Math.cos(a), Math.sin(a), 0).normalize();

        float elevation = (float) sunDir.y;
        goldenHour = Mth.clamp(1f - Math.abs(elevation) * 3.2f, 0f, 1f);

        Vec3 wind = WindDirection.get(0f);
        double speed = ClientAtmosphereState.getWindIntensity() * 0.35 + 0.05;
        scrollX += wind.x * speed;
        scrollZ += wind.z * speed;
    }

    public static float threshold() {
        float raw = 1f - coverage;
        return Math.round(raw / THRESHOLD_STEP) * THRESHOLD_STEP;
    }

    public static int tint() {
        float ambient = WeatherPalette.ambient();

        float r = mixedR * ambient;
        float g = mixedG * ambient;
        float b = mixedB * ambient;

        return ((int) (Mth.clamp(r, 0f, 1f) * 255) << 16)
                | ((int) (Mth.clamp(g, 0f, 1f) * 255) << 8)
                |  (int) (Mth.clamp(b, 0f, 1f) * 255);
    }

    private static Vec3 normalizedHue(int rgb) {
        double r = ((rgb >> 16) & 0xFF) / 255.0;
        double g = ((rgb >> 8)  & 0xFF) / 255.0;
        double b = ( rgb        & 0xFF) / 255.0;
        double lum = Math.max(0.001, (r + g + b) / 3.0);
        return new Vec3(r / lum, g / lum, b / lum);
    }

    public static float opacity() { return opacity; }
    public static float altitude()  { return altitude; }
    public static double scrollX()  { return scrollX; }
    public static double scrollZ()  { return scrollZ; }
    public static boolean enabled() { return coverage > 0.005f; }
    public static float sunAngle() { return sunAngle; }
    public static Vec3 sunDir() { return sunDir; }
    public static float goldenHour() { return goldenHour; }

    private static float altitudeFor(WeatherTypes t) {
        return switch (t) {
            case THUNDERSTORM, HEAVY_RAIN, BLIZZARD -> 128f;
            case OVERCAST, LIGHT_RAIN, SNOW, HAIL   -> 144f;
            case CLOUDY, DRIZZLE, FREEZING_RAIN     -> 160f;
            case SUNNY, WINDY                       -> 184f;
            case CLEAR                              -> 192f;
            default                                 -> 160f;
        };
    }

    private static float baseCoverage(WeatherTypes type) {
        return switch (type) {
            case CLEAR         -> 0.04f;
            case SUNNY         -> 0.12f;
            case WINDY         -> 0.26f;
            case MIST          -> 0.35f;
            case CLOUDY        -> 0.40f;
            case FOG           -> 0.50f;
            case DENSE_FOG     -> 0.60f;
            case DRIZZLE       -> 0.70f;
            case OVERCAST      -> 0.88f;
            case LIGHT_RAIN    -> 0.82f;
            case HEAVY_RAIN    -> 0.94f;
            case FREEZING_RAIN -> 0.90f;
            case THUNDERSTORM  -> 0.96f;
            case SNOW          -> 0.85f;
            case BLIZZARD      -> 0.97f;
            case HAIL          -> 0.92f;
            case SANDSTORM     -> 0.45f;
            default            -> 0f;
        };
    }

    private static float coverageMargin(WeatherTypes type) {
        return switch (type) {
            case CLEAR, THUNDERSTORM, BLIZZARD -> 0.04f;
            case CLOUDY, WINDY, MIST           -> 0.12f;
            default                            -> 0.08f;
        };
    }

    private static float opacityFor(WeatherTypes t) {
        return switch (t) {
            case THUNDERSTORM, BLIZZARD, HEAVY_RAIN -> 0.88f / 1.4f;
            case OVERCAST, FREEZING_RAIN, HAIL      -> 0.80f / 2f;
            case LIGHT_RAIN, SNOW, DRIZZLE          -> 0.72f / 1.4f;
            case CLOUDY, FOG, DENSE_FOG             -> 0.45f / 1.5f;
            case MIST, SANDSTORM                    -> 0.52f / 2f;
            case WINDY                              -> 0.45f / 1.5f;
            case SUNNY                              -> 0.40f / 2f;
            case CLEAR                              -> 0.32f / 2f;
            default                                 -> 0.60f / 2f;
        };
    }
}