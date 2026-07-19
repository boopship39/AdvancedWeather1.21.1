package net.antopfr.advancedweather.weather;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public enum DimensionProfile {
    OVERWORLD(
            950f, 1050f, 1013f,
            -30f, 55f,
            -20f, 28f, true
    ),

    NETHER(
            1080f, 1200f, 1150f,
            45f,   85f,
            -30f,  -10f, false
    ),

    END(
            850f,  950f,  900f,
            -60f,  -10f,
            -30f,  -20f, false
    );

    public final float pMin, pMax, pNominal;
    public final float tMin, tMax;
    public final float dpMin, dpMax;
    public final boolean hasDayCycle;

    DimensionProfile(float pMin, float pMax, float pNominal, float tMin, float tMax, float dpMin, float dpMax, boolean hasDayCycle) {
        this.pMin = pMin;
        this.pMax = pMax;
        this.pNominal = pNominal;
        this.tMin = tMin;
        this.tMax = tMax;
        this.dpMin = dpMin;
        this.dpMax = dpMax;
        this.hasDayCycle = hasDayCycle;
    }

    public static DimensionProfile of(ServerLevel level) {
        if (level.dimension().equals(Level.NETHER)) return NETHER;
        if (level.dimension().equals(Level.END))    return END;
        return OVERWORLD;
    }

    public static WeatherTypes.Dimension getDimension(ServerLevel level) {
        if (level.dimension().equals(Level.NETHER)) return WeatherTypes.Dimension.NETHER;
        if (level.dimension().equals(Level.END))    return WeatherTypes.Dimension.END;
        return WeatherTypes.Dimension.OVERWORLD;
    }
}
