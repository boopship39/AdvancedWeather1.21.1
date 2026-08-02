package net.antopfr.advancedweather.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.antopfr.advancedweather.util.AWRegistries;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionProfile {
    public static final Codec<DimensionProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("pressure_min").forGetter(p -> p.pMin),
            Codec.FLOAT.fieldOf("pressure_max").forGetter(p -> p.pMax),
            Codec.FLOAT.fieldOf("pressure_nominal").forGetter(p -> p.pNominal),
            Codec.FLOAT.fieldOf("temperature_min").forGetter(p -> p.tMin),
            Codec.FLOAT.fieldOf("temperature_max").forGetter(p -> p.tMax),
            Codec.FLOAT.fieldOf("dew_point_min").forGetter(p -> p.dpMin),
            Codec.FLOAT.fieldOf("dew_point_max").forGetter(p -> p.dpMax),
            Codec.BOOL.optionalFieldOf("has_day_cycle", true).forGetter(p -> p.hasDayCycle)
    ).apply(i, DimensionProfile::new));

    public static final DimensionProfile FALLBACK = new DimensionProfile(
            950f, 1050f, 1013f,
            -30f, 55f,
            -20f, 28f, true);

    public final float pMin, pMax, pNominal;
    public final float tMin, tMax;
    public final float dpMin, dpMax;
    public final boolean hasDayCycle;

    public DimensionProfile(float pMin, float pMax, float pNominal,
                            float tMin, float tMax,
                            float dpMin, float dpMax, boolean hasDayCycle) {
        this.pMin = Math.min(pMin, pMax);
        this.pMax = Math.max(pMin, pMax);
        this.pNominal = pNominal;
        this.tMin = Math.min(tMin, tMax);
        this.tMax = Math.max(tMin, tMax);
        this.dpMin = Math.min(dpMin, dpMax);
        this.dpMax = Math.max(dpMin, dpMax);
        this.hasDayCycle = hasDayCycle;
    }

    public static DimensionProfile of(Level level) {
        Registry<DimensionProfile> registry = level.registryAccess()
                .registry(AWRegistries.DIMENSION_PROFILE).orElse(null);
        if (registry == null) return FALLBACK;

        DimensionProfile profile = registry.get(level.dimension().location());
        return profile != null ? profile : FALLBACK;
    }

    public static WeatherTypes.Dimension getDimension(Level level) {
        if (level.dimension().equals(Level.NETHER)) return WeatherTypes.Dimension.NETHER;
        if (level.dimension().equals(Level.END))    return WeatherTypes.Dimension.END;
        return WeatherTypes.Dimension.OVERWORLD;
    }
}
