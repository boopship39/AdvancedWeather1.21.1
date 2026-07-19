package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.compat.sereneseasons.SeasonModifiers;
import net.antopfr.advancedweather.util.OpenSimplex2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AtmosphericSystem {

    public static final float P_MIN     = 950f;
    public static final float P_MAX     = 1050f;
    private static final float P_NOMINAL = 1013f;

    private static final float DP_MIN    = -20f;
    private static final float DP_MAX    = 28f;

    private static final long  SEED_P    = 0x4156L;
    private static final long  SEED_DP   = 0x9A3BL;

    private float pressure    = P_NOMINAL;
    private float pressureVel = 0f;
    private float dewPoint    = 8f;
    private float dewPointVel = 0f;

    public enum Mode { PROCEDURAL, REAL, MANUAL }
    private Mode  mode               = Mode.PROCEDURAL;
    private float softPMin           = P_MIN;
    private float softPMax           = P_MAX;
    private float softDPMin          = DP_MIN;
    private float softDPMax          = DP_MAX;
    private int   manualTicksRemaining = 0;

    private float lastTemperature = 15f;
    private float lastHumidity    = 60f;
    private float lastWindIntensity = 0f;

    private float gustOffset = 0f;
    private float gustTarget = 0f;
    private int   gustCooldown = 0;

    private final List<AtmosphericForcing> activeForcings = new ArrayList<>();

    private static long getDimensionSeedOffset(Level level) {
        if (level.dimension().equals(Level.NETHER)) return 5000L;
        if (level.dimension().equals(Level.END)) return 10000L;
        return 0L;
    }

    public void tick(ServerLevel level, WeatherTypes weatherType) {
        DimensionProfile profile = DimensionProfile.of(level);
        long dimensionOffset = getDimensionSeedOffset(level);

        double t = level.getGameTime() / 96000.0;
        long tod = level.getDayTime() % 24000;

        Iterator<AtmosphericForcing> it = activeForcings.iterator();
        while (it.hasNext()) {
            AtmosphericForcing f = it.next();
            pressureVel += f.pressurePushPerTick();
            dewPointVel += f.dewPointPushPerTick();
            if (!f.tick()) it.remove();
        }

        // PRESSURE
        float macroNoise = OpenSimplex2.noise2(SEED_P + dimensionOffset, t * 0.15, t * 0.3);
        float microNoise = OpenSimplex2.noise2(SEED_P + dimensionOffset + 42L, t * 0.8, t * 1.5);
        float combinedNoise = (macroNoise * 22.0f) + (microNoise * 6.0f);
        float pNoiseTarget = profile.pNominal + combinedNoise;

        float pTarget = switch (mode) {
            case PROCEDURAL    -> pNoiseTarget;
            case REAL, MANUAL  -> Mth.clamp(pNoiseTarget, softPMin, softPMax);
        };
        pTarget = Mth.clamp(pTarget, profile.pMin, profile.pMax);

        pressureVel += (pTarget - pressure) * 0.008f;
        pressureVel *= 0.91f;
        pressureVel  = Mth.clamp(pressureVel, -2.0f, 2.0f);
        pressure     = Mth.clamp(pressure + pressureVel, profile.pMin, profile.pMax);

        // DEW POINT
        float pNorm = (pressure - profile.pMin) / (profile.pMax - profile.pMin);
        float dpFromPressure = Mth.lerp(pNorm, profile.dpMax * 0.65f, profile.dpMin * 0.5f);
        float dpNoise = (float)(OpenSimplex2.noise2(SEED_DP + dimensionOffset, t * 0.5, t * 0.9) * 6.0);
        float dpNoiseTarget = dpFromPressure + dpNoise;
        float dpTarget = switch (mode) {
            case PROCEDURAL    -> dpNoiseTarget;
            case REAL, MANUAL  -> Mth.clamp(dpNoiseTarget, softDPMin, softDPMax);
        };
        dpTarget = Mth.clamp(dpTarget, profile.dpMin, profile.dpMax);

        dewPointVel += (dpTarget - dewPoint) * 0.005f;
        dewPointVel *= 0.94f;
        dewPointVel  = Mth.clamp(dewPointVel, -0.3f, 0.3f);
        dewPoint     = Mth.clamp(dewPoint + dewPointVel, profile.dpMin, profile.dpMax);

        // TEMPERATURE
        float weatherBase = getWeatherTempBase(weatherType);
        float seasonOffset = SeasonModifiers.isSeasonalType(weatherType)
                ? 0f
                : SeasonModifiers.temperatureOffset(level);

        float solarAmplitude = computeSolarAmplitude(dewPoint);
        float solarEffect = profile.hasDayCycle
                ? (float)Math.cos((tod - 6000) * Math.PI / 12000.0) * solarAmplitude
                : 0f;

        float adiabaticEffect = (pNorm - 0.5f) * 4f;
        lastTemperature = Mth.clamp(weatherBase + seasonOffset + forcingTempOffset() + solarEffect + adiabaticEffect, profile.tMin, profile.tMax);

        // HUMIDITY
        lastHumidity = Mth.clamp(computeRelativeHumidity(lastTemperature, dewPoint), 0f, 100f);

        // WIND
        float pressureGradient = Math.abs(pressureVel);
        float baseWindFromWeather = getWeatherWindBase(weatherType);
        float gradientWind = Mth.clamp(pressureGradient / 0.8f, 0f, 1f);

        float rawWindBase = Mth.clamp(Math.max(baseWindFromWeather, gradientWind * 0.75f + baseWindFromWeather * 0.25f), 0f, 1f);

        if (gustCooldown > 0) {
            gustCooldown--;
        }

        gustOffset += (gustTarget - gustOffset) * 0.08f;

        if (gustCooldown <= 0 && gustTarget != 0f) {
            gustTarget = 0f;
        }

        float gustChance = 0.005f + rawWindBase * 0.02f;
        if (gustCooldown <= 0 && level.getRandom().nextFloat() < gustChance) {
            gustTarget = rawWindBase * (0.15f + level.getRandom().nextFloat() * 0.25f);
            gustCooldown = 30 + level.getRandom().nextInt((int) (60 * (1f - rawWindBase) + 20));
        }

        lastWindIntensity = Mth.clamp(rawWindBase + gustOffset, 0f, 1f);

        if (mode == Mode.MANUAL) {
            manualTicksRemaining--;
            if (manualTicksRemaining <= 0) mode = Mode.PROCEDURAL;
        }
    }

    private static float computeRelativeHumidity(float tempC, float dewPointC) {
        double eT  = Math.exp(17.27 * tempC    / (tempC    + 237.3));
        double eTd = Math.exp(17.27 * dewPointC / (dewPointC + 237.3));
        return (float)(100.0 * eTd / eT);
    }

    private static float computeSolarAmplitude(float dewPointC) {
        float normalized = Mth.clamp((dewPointC - DP_MIN) / (DP_MAX - DP_MIN), 0f, 1f);
        return Mth.lerp(normalized, 8f, 3f);
    }

    // BASE VALUES
    private static float getWeatherTempBase(WeatherTypes type) {
        return switch (type) {
            // OVERWORLD
            case CLEAR         -> 16f;
            case SUNNY         -> 14f;
            case CLOUDY        -> 12f;
            case MIST          -> 10f;
            case OVERCAST      -> 9f;
            case FOG           -> 8f;
            case DENSE_FOG     -> 7f;
            case DRIZZLE       -> 9f;
            case LIGHT_RAIN    -> 8f;
            case HEAVY_RAIN    -> 6f;
            case FREEZING_RAIN -> 0f;
            case THUNDERSTORM  -> 13f;
            case SNOW          -> -3f;
            case BLIZZARD      -> -6f;
            case HAIL          -> 12f;
            case WINDY         -> 13f;
            case SANDSTORM     -> 30f;

            // NETHER
            case ASH_STORM        -> 85f;
            case BRIMSTONE_STORM  -> 75f;
            case LAVA_RAIN        -> 100f;
            case NETHERSTORM      -> 80f;
            case HELLFIRE         -> 115f;

            // END
            case VOID_STORM       -> -20f;
            case END_MIST         -> -15f;
            case CHORUS_GALE      -> -18f;
            case ENDERSTORM       -> -25f;

            default -> 15f;
        };
    }

    private static float getWeatherWindBase(WeatherTypes type) {
        return switch (type) {
            // OVERWORLD
            case THUNDERSTORM, BLIZZARD          -> 0.75f;
            case SANDSTORM, WINDY                -> 0.65f;
            case HEAVY_RAIN, HAIL                -> 0.45f;
            case LIGHT_RAIN, DRIZZLE,
                 FREEZING_RAIN, OVERCAST, SNOW   -> 0.15f;
            case CLEAR, CLOUDY, MIST,
                 FOG, DENSE_FOG                  -> 0.05f;

            // NETHER
            case BRIMSTONE_STORM, NETHERSTORM -> 0.80f;
            case ASH_STORM                    -> 0.60f;
            case LAVA_RAIN                    -> 0.40f;
            case HELLFIRE                     -> 0.15f;

            // END
            case VOID_STORM, ENDERSTORM       -> 0.70f;
            case CHORUS_GALE                  -> 0.85f;
            case END_MIST                     -> 0.20f;

            default -> 0.10f;
        };
    }

    // ALL MODES

    public void setProceduralMode() {
        mode = Mode.PROCEDURAL;
        softPMin = P_MIN; softPMax = P_MAX;
        softDPMin = DP_MIN; softDPMax = DP_MAX;
    }

    public void setManualMode(WeatherTypes type, ServerLevel level) {
        mode = Mode.MANUAL;
        manualTicksRemaining = 12000;
        applyWeatherConstraints(type, level);
        pressure = getRecommendedPressure(type);
        pressureVel = 0f;
        dewPoint = getRecommendedDewPoint(type, level);
        dewPointVel = 0f;
    }

    public void setRealWeatherMode(WeatherTypes type, ServerLevel level) {
        mode = Mode.REAL;
        applyWeatherConstraints(type, level);
        float targetP = getRecommendedPressure(type);
        float targetDP = getRecommendedDewPoint(type, level);
        if (pressure < softPMin || pressure > softPMax)
            pressureVel += (targetP - pressure) * 0.05f;
        if (dewPoint < softDPMin || dewPoint > softDPMax)
            dewPointVel += (targetDP - dewPoint) * 0.05f;
    }

    private void applyWeatherConstraints(WeatherTypes type, ServerLevel level) {
        float pCenter = getRecommendedPressure(type);
        float dpCenter = getRecommendedDewPoint(type, level);
        float pMargin = 15f;
        float dpMargin = 4f;
        softPMin = Mth.clamp(pCenter  - pMargin,  P_MIN,  P_MAX);
        softPMax = Mth.clamp(pCenter  + pMargin,  P_MIN,  P_MAX);
        softDPMin = Mth.clamp(dpCenter - dpMargin, DP_MIN, DP_MAX);
        softDPMax = Mth.clamp(dpCenter + dpMargin, DP_MIN, DP_MAX);
    }

    public void snapToTarget(ServerLevel level, WeatherTypes type) {
        DimensionProfile profile = DimensionProfile.of(level);
        long   gameTime = level.getGameTime();
        double t = gameTime / 96000.0;
        long dimensionOffset = getDimensionSeedOffset(level);

        float  pNoise = profile.pNominal + (float)(OpenSimplex2.noise2(SEED_P + dimensionOffset, t * 0.25, t * 0.6) * 28.0);
        pressure = Mth.clamp(pNoise, softPMin, softPMax);
        pressureVel = 0f;
        float pNorm = (pressure - profile.pMin) / (profile.pMax - profile.pMin);
        float dpBase = Mth.lerp(pNorm, profile.dpMax * 0.7f, profile.dpMin * 0.5f);
        dewPoint = Mth.clamp(dpBase, softDPMin, softDPMax);
        dewPointVel = 0f;
    }

    // RECOMMENDED VALUES
    public static float getRecommendedPressure(WeatherTypes type) {
        return switch (type) {
            // OVERWORLD
            case CLEAR, SUNNY, WINDY                 -> 1035f;
            case CLOUDY, MIST                        -> 1015f;
            case OVERCAST, FOG, DENSE_FOG            -> 1005f;
            case DRIZZLE, LIGHT_RAIN, SNOW           -> 995f;
            case HEAVY_RAIN, BLIZZARD, FREEZING_RAIN -> 980f;
            case THUNDERSTORM, SANDSTORM, HAIL       -> 962f;

            // NETHER
            case ASH_STORM           -> 1130f;
            case BRIMSTONE_STORM     -> 1160f;
            case LAVA_RAIN           -> 1140f;
            case NETHERSTORM         -> 1170f;
            case HELLFIRE            -> 1180f;

            // END
            case VOID_STORM          -> 890f;
            case END_MIST            -> 920f;
            case CHORUS_GALE         -> 880f;
            case ENDERSTORM          -> 870f;

            default -> P_NOMINAL;
        };
    }

    public static float getRecommendedDewPoint(WeatherTypes type) {
        return switch (type) {
            //OVERWORLD
            case CLEAR, SUNNY      -> 5f;
            case WINDY             -> 4f;
            case CLOUDY, MIST      -> 10f;
            case OVERCAST          -> 12f;
            case FOG, DENSE_FOG    -> 14f;
            case DRIZZLE           -> 13f;
            case LIGHT_RAIN        -> 14f;
            case HEAVY_RAIN        -> 18f;
            case THUNDERSTORM      -> 20f;
            case FREEZING_RAIN     -> -1f;
            case SNOW              -> -5f;
            case BLIZZARD          -> -8f;
            case HAIL              -> 15f;
            case SANDSTORM         -> -8f;

            // NETHER
            case NETHER_CLEAR, ASH_STORM, BRIMSTONE_STORM, LAVA_RAIN, NETHERSTORM, HELLFIRE -> -25f;

            // END
            case END_CLEAR, VOID_STORM, END_MIST, CHORUS_GALE, ENDERSTORM -> -28f;

            default -> 8.0F;
        };
    }

    public static float getRecommendedDewPoint(WeatherTypes type, ServerLevel level) {
        float base = getRecommendedDewPoint(type);
        float offset = SeasonModifiers.dewPointOffset(level);
        return Mth.clamp(base + offset, DP_MIN, DP_MAX);
    }

    // FORECAST
    public AtmosphericSystem forecastState(int inGameMinutes, long currentGameTime, ServerLevel level) {
        DimensionProfile profile = DimensionProfile.of(level);
        long dimensionOffset = getDimensionSeedOffset(level);

        float p    = pressure;
        float pv   = pressureVel;
        float dp   = dewPoint;
        float dpv  = dewPointVel;

        int steps    = inGameMinutes * 60;
        int stepSize = 20;

        for (int i = 0; i < steps; i++) {
            long   ft     = currentGameTime + (long)i * stepSize;
            double fT     = ft / 96000.0;

            float pTarget = profile.pNominal + (float)(OpenSimplex2.noise2(SEED_P + dimensionOffset, fT * 0.25, fT * 0.6) * 45.0);
            pTarget = Mth.clamp(pTarget, softPMin, softPMax);

            pv += (pTarget - p) * 0.006f * stepSize;
            pv *= (float)Math.pow(0.93f, stepSize);
            pv  = Mth.clamp(pv, -1.5f, 1.5f);
            p   = Mth.clamp(p + pv, profile.pMin, profile.pMax);

            float pNorm = (p - profile.pMin) / (profile.pMax - profile.pMin);
            float dpBase   = Mth.lerp(pNorm, profile.dpMax * 0.7f, profile.dpMin * 0.5f);
            float dpTarget = dpBase + (float)(OpenSimplex2.noise2(SEED_DP + dimensionOffset, fT * 0.4, fT * 0.7) * 8.0);
            dpTarget = Mth.clamp(dpTarget, softDPMin, softDPMax);

            dpv += (dpTarget - dp) * 0.004f * stepSize;
            dpv *= (float)Math.pow(0.94f, stepSize);
            dpv  = Mth.clamp(dpv, -0.3f, 0.3f);
            dp   = Mth.clamp(dp + dpv, profile.dpMin, profile.dpMax);
        }

        AtmosphericSystem future = new AtmosphericSystem();
        future.pressure    = p;
        future.pressureVel = pv;
        future.dewPoint    = dp;
        future.dewPointVel = dpv;
        return future;
    }

    public float forecastTemperatureFromState(AtmosphericSystem state, int min, long t, WeatherTypes wt, ServerLevel level) {
        DimensionProfile profile = DimensionProfile.of(level);
        long tod = (level.getDayTime() + min * 1200L) % 24000;
        float solar = (float)Math.cos((tod - 6000) * Math.PI / 12000.0);
        float amp   = computeSolarAmplitude(state.dewPoint);
        float se    = profile.hasDayCycle ? (solar > 0 ? solar * amp : solar * amp * 0.6f) : 0f;
        float pNorm = (state.pressure - profile.pMin) / (profile.pMax - profile.pMin);
        return Mth.clamp(getWeatherTempBase(wt) + se + (pNorm - 0.5f) * 4f, profile.tMin, profile.tMax);
    }

    public float forecastHumidityFromState(AtmosphericSystem state, WeatherTypes wt, int min, long t, ServerLevel level) {
        float futureT = forecastTemperatureFromState(state, min, t, wt, level);
        return Mth.clamp(computeRelativeHumidity(futureT, state.dewPoint), 0f, 100f);
    }

    public float getNormalized(ServerLevel level) {
        DimensionProfile profile = DimensionProfile.of(level);
        return (pressure - profile.pMin) / (profile.pMax - profile.pMin);
    }

    public PressureCategory getCategory(ServerLevel level) {
        DimensionProfile profile = DimensionProfile.of(level);
        float range = profile.pMax - profile.pMin;
        if (pressure > profile.pMin + range * 0.7f) return PressureCategory.HIGH;
        if (pressure > profile.pMin + profile.pNominal) return PressureCategory.NEUTRAL;
        if (pressure > profile.pMin + range * 0.3f) return PressureCategory.LOW;
        return PressureCategory.STORM;
    }

    public enum PressureCategory { HIGH, NEUTRAL, LOW, STORM }

    public float forecastPressure(int min, long t, ServerLevel level) {
        return forecastState(min, t, level).getPressure();
    }

    public float forecastTemperature(int min, long t, WeatherTypes wt, ServerLevel level) {
        DimensionProfile profile = DimensionProfile.of(level);
        AtmosphericSystem f = forecastState(min, t, level);

        long tod = (level.getDayTime() + min * 1200L) % 24000;
        float solar = (float)Math.cos((tod - 6000) * Math.PI / 12000.0);
        float amp   = computeSolarAmplitude(f.dewPoint);
        float se    = profile.hasDayCycle ? (solar > 0 ? solar * amp : solar * amp * 0.6f) : 0f;
        float pNorm = (f.pressure - profile.pMin) / (profile.pMax - profile.pMin);

        return Mth.clamp(getWeatherTempBase(wt) + se + (pNorm - 0.5f) * 4f, profile.tMin, profile.tMax);
    }

    public float forecastHumidity(int min, long t, WeatherTypes wt, ServerLevel level) {
        AtmosphericSystem f = forecastState(min, t, level);
        float futureT = forecastTemperature(min, t, wt, level);
        return Mth.clamp(computeRelativeHumidity(futureT, f.dewPoint), 0f, 100f);
    }

    public float getPressure()      { return pressure; }
    public float getPressureVel()   { return pressureVel; }
    public float getDewPoint()      { return dewPoint; }
    public float getTemperature()   { return lastTemperature; }
    public float getHumidity()      { return lastHumidity; }
    public float getWindIntensity() { return lastWindIntensity; }
    public Mode  getMode()          { return mode; }

    public void applyForcing(float totalPressurePush, float totalDewPointPush,
                             float tempOffset, int durationTicks) {
        activeForcings.add(new AtmosphericForcing(
                totalPressurePush, totalDewPointPush, tempOffset, durationTicks));
    }

    public int getActiveForcingCount() { return activeForcings.size(); }

    public void clearForcings() { activeForcings.clear(); }

    private float forcingTempOffset() {
        float sum = 0f;
        for (AtmosphericForcing f : activeForcings) sum += f.currentTempOffset();
        return sum;
    }

    // NBT

    public void save(CompoundTag tag) {
        tag.putFloat("pressure",    pressure);
        tag.putFloat("pressureVel", pressureVel);
        tag.putFloat("dewPoint",    dewPoint);
        tag.putFloat("dewPointVel", dewPointVel);
        tag.putString("atmosMode",  mode.name());
        tag.putFloat("softPMin",    softPMin);
        tag.putFloat("softPMax",    softPMax);
        tag.putFloat("softDPMin",   softDPMin);
        tag.putFloat("softDPMax",   softDPMax);
        tag.putInt("manualTicks",   manualTicksRemaining);
        ListTag forcings = new ListTag();
        for (AtmosphericForcing f : activeForcings) forcings.add(f.save());
        tag.put("Forcings", forcings);
    }

    public void load(CompoundTag tag) {
        pressure    = tag.contains("pressure")    ? tag.getFloat("pressure")    : P_NOMINAL;
        pressureVel = tag.contains("pressureVel") ? tag.getFloat("pressureVel") : 0f;
        dewPoint    = tag.contains("dewPoint")    ? tag.getFloat("dewPoint")    : 8f;
        dewPointVel = tag.contains("dewPointVel") ? tag.getFloat("dewPointVel") : 0f;
        softPMin    = tag.contains("softPMin")    ? tag.getFloat("softPMin")    : P_MIN;
        softPMax    = tag.contains("softPMax")    ? tag.getFloat("softPMax")    : P_MAX;
        softDPMin   = tag.contains("softDPMin")   ? tag.getFloat("softDPMin")   : DP_MIN;
        softDPMax   = tag.contains("softDPMax")   ? tag.getFloat("softDPMax")   : DP_MAX;
        manualTicksRemaining = tag.contains("manualTicks") ? tag.getInt("manualTicks") : 0;
        mode = tag.contains("atmosMode")
                ? Mode.valueOf(tag.getString("atmosMode"))
                : Mode.PROCEDURAL;
        activeForcings.clear();
        ListTag forcingList = tag.getList("Forcings", Tag.TAG_COMPOUND);
        for (Tag t : forcingList) activeForcings.add(AtmosphericForcing.load((CompoundTag) t));
    }
}