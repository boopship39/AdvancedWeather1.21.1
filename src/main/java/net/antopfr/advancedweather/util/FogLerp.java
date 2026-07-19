package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.weather.WeatherTypes;

public class FogLerp {
    private float currentNear;
    private float currentFar;
    private boolean shouldRenderCustomFog;

    public void update(WeatherTypes prev, WeatherTypes current, float progress, float renderDistance) {
        if (!prev.hasFog() && !current.hasFog()) {
            this.shouldRenderCustomFog = false;
            return;
        }

        if (progress >= 1.0f && !current.hasFog()) {
            this.shouldRenderCustomFog = false;
            return;
        }

        this.shouldRenderCustomFog = true;

        float prevNear = getNearDistanceFor(prev, renderDistance);
        float prevFar  = getFarDistanceFor(prev, renderDistance);
        float currentNear = getNearDistanceFor(current, renderDistance);
        float currentFar  = getFarDistanceFor(current, renderDistance);
        this.currentNear = prevNear + (currentNear - prevNear) * progress;
        this.currentFar  = prevFar + (currentFar - prevFar) * progress;
    }

    public boolean shouldRenderCustomFog() {
        return this.shouldRenderCustomFog;
    }

    public float getCurrentNear() {
        return this.currentNear;
    }

    public float getCurrentFar() {
        return this.currentFar;
    }

    private static float getNearDistanceFor(WeatherTypes type, float renderDistance) {
        if (!type.hasFog()) {
            return renderDistance;
        }

        AWClientConfig config = AWClientConfig.get();
        return (float) switch (type) {
            // OVERWORLD
            case FOG           -> config.fogNear;
            case MIST          -> config.mistFogNear;
            case BLIZZARD      -> config.blizzardFogNear;
            case DENSE_FOG     -> config.denseFogNear;
            case FREEZING_RAIN -> config.freezingRainFogNear;
            case THUNDERSTORM  -> config.thunderstormFogNear;
            case SANDSTORM     -> config.sandstormFogNear;
            case HAIL          -> config.hailFogNear;

            // NETHER
            case BRIMSTONE_STORM -> 4.0f;
            case ASH_STORM       -> 5.0f;
            case NETHERSTORM     -> 20.0f;
            case HELLFIRE        -> 12.0f;

            default -> renderDistance * 0.75f;
        };
    }

    private static float getFarDistanceFor(WeatherTypes type, float renderDistance) {
        if (!type.hasFog()) {
            return renderDistance;
        }

        AWClientConfig config = AWClientConfig.get();
        return (float) switch (type) {
            // OVERWORLD (config)
            case FOG           -> config.fogFar;
            case MIST          -> config.mistFogFar;
            case BLIZZARD      -> config.blizzardFogFar;
            case DENSE_FOG     -> config.denseFogFar;
            case FREEZING_RAIN -> config.freezingRainFogFar;
            case THUNDERSTORM  -> config.thunderstormFogFar;
            case SANDSTORM     -> config.sandstormFogFar;
            case HAIL          -> config.hailFogFar;

            // NETHER
            case BRIMSTONE_STORM -> Math.min(renderDistance, 24.0f);
            case ASH_STORM       -> Math.min(renderDistance, 22.0f);
            case NETHERSTORM     -> Math.min(renderDistance, 50.0f);
            case HELLFIRE        -> Math.min(renderDistance, 64.0f);

            default -> renderDistance;
        };
    }
}
