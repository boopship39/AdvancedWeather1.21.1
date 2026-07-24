package net.antopfr.advancedweather.weather;

import net.antopfr.advancedweather.api.event.WeatherChangeEvent;
import net.antopfr.advancedweather.api.external.NominatimGeocoding;
import net.antopfr.advancedweather.api.external.OpenMeteo;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.advancement.AWAdvancements;
import net.antopfr.advancedweather.network.toclient.*;
import net.antopfr.advancedweather.util.AWLogger;
import net.antopfr.advancedweather.weather.effect.EffectManager;
import net.antopfr.advancedweather.weather.effect.types.rainbows.RainbowSpawner;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeatherManager extends SavedData {

    private static final String NAME = "advancedweather";
    private final Random random = new Random();

    // PER DIM
    private final Map<WeatherTypes.Dimension, WeatherTypes> currentWeathers = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, Integer> weatherTimers = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, Long> lastGameTicks = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, Float> windIntensities = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, AtmosphericSystem> atmospheres = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, WeatherHistory> histories = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, WeatherDurationTracker> durationTrackers = new EnumMap<>(WeatherTypes.Dimension.class);
    private final Map<WeatherTypes.Dimension, Deque<WeatherTypes>> recentHistory = new EnumMap<>(WeatherTypes.Dimension.class);

    private static final int HISTORY_SIZE = 5;

    // REAL MODE
    private final Map<WeatherTypes.Dimension, Integer> realWeatherTickTimers = new EnumMap<>(WeatherTypes.Dimension.class);
    private boolean realWeatherWarningLogged = false;

    private final Map<WeatherTypes.Dimension, WeatherTypes> lastPredictedNext = new EnumMap<>(WeatherTypes.Dimension.class);

    public WeatherManager() {
        for (WeatherTypes.Dimension dim : WeatherTypes.Dimension.values()) {
            currentWeathers.put(dim, defaultForDimension(dim));
            weatherTimers.put(dim, -1);
            lastGameTicks.put(dim, -1L);
            windIntensities.put(dim, 0f);
            atmospheres.put(dim, new AtmosphericSystem());
            histories.put(dim, new WeatherHistory());
            durationTrackers.put(dim, new WeatherDurationTracker());
            realWeatherTickTimers.put(dim, 0);
            recentHistory.put(dim, new ArrayDeque<>());
        }
    }

    private static WeatherTypes defaultForDimension(WeatherTypes.Dimension dim) {
        return switch (dim) {
            case NETHER -> WeatherTypes.NETHER_CLEAR;
            case END    -> WeatherTypes.END_CLEAR;
            default     -> WeatherTypes.CLEAR;
        };
    }

    public float getWindIntensity(ServerLevel level) { return windIntensities.getOrDefault(DimensionProfile.getDimension(level), 0f); }
    public AtmosphericSystem getAtmosphere(ServerLevel level) { return atmospheres.get(DimensionProfile.getDimension(level)); }
    public WeatherHistory getHistory(ServerLevel level) { return histories.get(DimensionProfile.getDimension(level)); }
    public WeatherDurationTracker getDurationTracker(ServerLevel level) { return durationTrackers.get(DimensionProfile.getDimension(level)); }
    public WeatherTypes getCurrentWeather(ServerLevel level) { return currentWeathers.getOrDefault(DimensionProfile.getDimension(level), WeatherTypes.CLEAR); }
    public List<WeatherTypes> getRecentHistory(ServerLevel level) { return new ArrayList<>(recentHistory.get(DimensionProfile.getDimension(level))); }

    // TICK
    public void tick(ServerLevel level) {
        AWCommonConfig config = AWCommonConfig.get();
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);

        WeatherTypes currentWeather = currentWeathers.get(dim);
        AtmosphericSystem atmosphere = atmospheres.get(dim);
        WeatherHistory history = histories.get(dim);
        WeatherDurationTracker durationTracker = durationTrackers.get(dim);
        float windIntensity = windIntensities.getOrDefault(dim, 0f);

        long gameTick = level.getGameTime();
        long delta = lastGameTicks.get(dim) == -1L ? 0 : gameTick - lastGameTicks.get(dim);
        lastGameTicks.put(dim, gameTick);

        if (delta > 100) {
            atmosphere.snapToTarget(level, currentWeather);
        }

        // Atmosphere Engine Tick
        atmosphere.tick(level, currentWeather);

        // Wind update
        windIntensity += (atmosphere.getWindIntensity() - windIntensity) * 0.02f;
        windIntensities.put(dim, windIntensity);

        // History recording
        history.tick(gameTick, atmosphere.getPressure(), windIntensity,
                atmosphere.getTemperature(), atmosphere.getHumidity(),
                currentWeather, dim);

        if (gameTick % 200 == 0 && !history.getEntries().isEmpty()) {
            syncPacketToDimension(level, new HistorySyncPacket(List.of(history.getEntries().getLast())));
        }

        // Sync network packets
        syncAtmosphereToClients(level);

        // Effects
        if (gameTick % 40 == 0) {
            EffectManager.get(level).resync(level);
        }

        // Duration Tracker
        durationTracker.tick(currentWeather);

        // Real weather mode (ONLY OVERWORLD)
        if (config.realWeatherEnabled) {
            if (dim == WeatherTypes.Dimension.OVERWORLD) {
                int realTimer = realWeatherTickTimers.get(dim) - 1;
                if (realTimer <= 0) {
                    realTimer = config.syncIntervalMinutes * 60 * 20;
                    fetchRealWeather(level);
                }
                realWeatherTickTimers.put(dim, realTimer);
                return;
            }
        }

        // Procedural mode
        int ticksRemaining = weatherTimers.get(dim);

        RainbowSpawner.onWeatherTick(level, currentWeather, gameTick);

        if (ticksRemaining < 0) return;

        ticksRemaining--;
        if (ticksRemaining <= 0) {
            WeatherTypes previous = currentWeather;

            List<WeatherTransitionGraph.Transition> available = WeatherTransitionGraph.getTransitions(currentWeather);
            WeatherTypes next;

            if (available.isEmpty()) {
                next = defaultForDimension(dim);
            } else {
                next = WeatherTransitionGraph.nextWeather(currentWeather, random, atmosphere, dim, level,
                        new ArrayList<>(recentHistory.get(dim)));
            }

            if (!isCompatibleWithLevel(next, level)) {
                next = defaultForDimension(dim);
            }

            currentWeathers.put(dim, next);
            pushHistory(dim, next);
            weatherTimers.put(dim, WeatherTransitionGraph.randomDuration(next, random, level.getGameTime() % 24000, level));
            this.setDirty();

            applyToLevel(level, next);
            syncToClients(level, next);

            fireWeatherChange(level, dim, previous, next);

            if (next != previous && next == lastPredictedNext.get(dim)) {
                for (ServerPlayer p : level.players()) AWAdvancements.grant(p, "told_you_so");
            }

            RainbowSpawner.onWeatherTransition(level, dim, previous, next);
        } else {
            weatherTimers.put(dim, ticksRemaining);
        }
    }

    // ALL MODES CONTROL
    public void setCurrentWeather(ServerLevel level, WeatherTypes type) {
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        WeatherTypes previous = this.currentWeathers.get(dim);
        this.currentWeathers.put(dim, type);
        this.weatherTimers.put(dim, -1);
        this.setDirty();
        atmospheres.get(dim).setManualMode(type, level);
        applyToLevel(level, type);
        syncToClients(level, type);
        fireWeatherChange(level, dim, previous, type);
    }

    public void applyRealWeather(ServerLevel level, WeatherTypes type) {
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        WeatherTypes previous = this.currentWeathers.get(dim);
        this.currentWeathers.put(dim, type);
        this.weatherTimers.put(dim, -1);
        this.setDirty();
        atmospheres.get(dim).setRealWeatherMode(type, level);
        applyToLevel(level, type);
        syncToClients(level, type);
        fireWeatherChange(level, dim, previous, type);

        for (ServerPlayer p : level.players()) AWAdvancements.grant(p, "look_outside");
    }

    private static void fireWeatherChange(ServerLevel level, WeatherTypes.Dimension dim,
                                          WeatherTypes previous, WeatherTypes next) {
        if (previous == next) return;
        NeoForge.EVENT_BUS.post(new WeatherChangeEvent(level, dim, previous, next));
    }

    public void startAutoWeather(ServerLevel level) {
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        atmospheres.get(dim).setProceduralMode();

        AWCommonConfig config = AWCommonConfig.get();
        if (dim == WeatherTypes.Dimension.OVERWORLD && config.realWeatherEnabled) {
            realWeatherTickTimers.put(dim, 0);
            fetchRealWeather(level);
        }

        WeatherTypes current = currentWeathers.get(dim);
        weatherTimers.put(dim, WeatherTransitionGraph.randomDuration(current, random, level.getGameTime() % 24000, level));
        setDirty();
    }

    // REAL WEATHER FETCH
    private void fetchRealWeather(ServerLevel level) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        AWCommonConfig config = AWCommonConfig.get();
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        OpenMeteo.fetchAsync(config.latitude, config.longitude)
                .thenAccept(type -> level.getServer().execute(() -> {
                    WeatherTypes currentWeather = currentWeathers.get(dim);
                    if (type == null) {
                        if (!realWeatherWarningLogged) {
                            AWLogger.AW.warn("[AdvancedWeather] Real weather unavailable, falling back to procedural.");
                            realWeatherWarningLogged = true;
                        }
                        if (weatherTimers.get(dim) < 0)
                            weatherTimers.put(dim, WeatherTransitionGraph.randomDuration(currentWeather, random, level.getGameTime() % 24000, level));
                        return;
                    }
                    realWeatherWarningLogged = false;
                    if (!config.realWeatherOverridesCommands && weatherTimers.get(dim) < 0) return;
                    if (type != currentWeather) applyRealWeather(level, type);
                }));
        NominatimGeocoding.fetchLocationNameAsync(config.latitude, config.longitude);
    }

    // APPLY
    private void applyToLevel(ServerLevel level, WeatherTypes weather) {
        boolean raining   = weather.isVanillaRaining();
        boolean thundering = weather.isVanillaThundering();

        level.setWeatherParameters(
                raining ? 0 : 6000,
                raining ? 6000 : 0,
                raining, thundering);

        level.getGameRules().getRule(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT)
                .set(weather == WeatherTypes.BLIZZARD ? 8 : 1, level.getServer());

        Set<WeatherEffects> effects = switch (weather) {
            //OVERWORLD
            case BLIZZARD             -> EnumSet.of(WeatherEffects.WIND_LINES, WeatherEffects.BLIZZARD_PARTICLES);
            case SANDSTORM            -> EnumSet.of(WeatherEffects.WIND_LINES, WeatherEffects.SAND_PARTICLES, WeatherEffects.TUMBLEWEEDS);
            case FOG, DENSE_FOG, MIST -> EnumSet.of(WeatherEffects.WIND_LINES, WeatherEffects.GROUND_FOG);
            case CLEAR, SUNNY, DRIZZLE-> EnumSet.of(WeatherEffects.WIND_LINES, WeatherEffects.RAINBOWS);
            //NETHER
            case NETHER_CLEAR         -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES);
            case BRIMSTONE_STORM      -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES, WeatherEffects.BRIMSTONE_PARTICLES);
            case ASH_STORM            -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES, WeatherEffects.ASH_PARTICLES);
            case LAVA_RAIN            -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES);
            case NETHERSTORM          -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES, WeatherEffects.FIRE_PARTICLES);
            case HELLFIRE             -> EnumSet.of(WeatherEffects.NETHER_WIND_LINES, WeatherEffects.HEAT_SHIMMER);
            //END
            case END_CLEAR              -> EnumSet.of(WeatherEffects.END_WIND_LINES);
            case CHORUS_GALE            -> EnumSet.of(WeatherEffects.END_WIND_LINES, WeatherEffects.CHORUS_PLANTS);
            case VOID_STORM             -> EnumSet.of(WeatherEffects.END_WIND_LINES, WeatherEffects.VOID_PARTICLES);
            case END_MIST               -> EnumSet.of(WeatherEffects.END_WIND_LINES, WeatherEffects.END_GROUND_FOG);
            case ENDERSTORM             -> EnumSet.of(WeatherEffects.END_WIND_LINES, WeatherEffects.ENDER_PARTICLES, WeatherEffects.CHROMATIC_EFFECT);

            default                     -> EnumSet.of(WeatherEffects.WIND_LINES);
        };

        EffectManager.get(level).setEffects(level, effects);
    }

    public static boolean isCompatibleWithLevel(WeatherTypes type, ServerLevel level) {
        if (level.dimension().equals(Level.NETHER)) return type.isNether();
        if (level.dimension().equals(Level.END))    return type.isEnd();
        return type.isOverworld();
    }

    private void pushHistory(WeatherTypes.Dimension dim, WeatherTypes type) {
        Deque<WeatherTypes> deque = recentHistory.get(dim);
        deque.addLast(type);
        if (deque.size() > HISTORY_SIZE) deque.removeFirst();
    }

    private void syncToClients(ServerLevel level, WeatherTypes weather) {
        syncPacketToDimension(level, new WeatherSyncPacket(weather));
    }

    private void syncAtmosphereToClients(ServerLevel level) {
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        AtmosphericSystem a = atmospheres.get(dim);
        WeatherTypes currentWeather = currentWeathers.get(dim);
        long time = level.getGameTime();

        AtmosphericSystem forecast = a.forecastState(30, time, level);
        List<WeatherTypes> history = new ArrayList<>(recentHistory.get(dim));

        WeatherTransitionGraph.Prediction predictionNext =
                WeatherTransitionGraph.mostLikelyNextWithWeights(currentWeather, a, dim, level, history);
        WeatherTransitionGraph.Prediction predictionIn30 =
                WeatherTransitionGraph.mostLikelyNextWithWeights(currentWeather, forecast, dim, level, history);

        List<TransitionProbabilitiesPacket.Entry> probabilities =
                WeatherTransitionGraph.computeProbabilities(currentWeather, a, dim, level, history);

        float confidenceNext = ForecastConfidence.compute(
                predictionNext.transitions(), predictionNext.weights(), a, 0);
        float confidenceIn30 = ForecastConfidence.compute(
                predictionIn30.transitions(), predictionIn30.weights(), forecast, 30);

        WeatherTypes predictedNext = predictionNext.type();
        WeatherTypes predictedIn30 = predictionIn30.type();

        lastPredictedNext.put(dim, predictedNext);

        syncPacketToDimension(level, new PressureSyncPacket(
                a.getPressure(), a.getPressureVel(),
                forecast.getPressure(),
                a.getCategory(level).name(),
                predictedNext.name(), predictedIn30.name(),
                a.getWindIntensity(), a.getMode().name(),
                confidenceNext, confidenceIn30
        ));
        syncPacketToDimension(level, new TemperatureSyncPacket(
                a.getTemperature(), a.forecastTemperatureFromState(forecast, 30, time, currentWeather, level)));
        syncPacketToDimension(level, new HumiditySyncPacket(
                a.getHumidity(), a.forecastHumidityFromState(forecast, currentWeather, 30, time, level)));
        syncPacketToDimension(level, new TransitionProbabilitiesPacket(probabilities));
    }

    private void syncPacketToDimension(ServerLevel level, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersInDimension(level, packet);
    }

    // NBT SAVE & LOAD
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        for (WeatherTypes.Dimension dim : WeatherTypes.Dimension.values()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("weatherType", currentWeathers.get(dim).name());
            dimTag.putInt("weatherTicksRemaining", weatherTimers.get(dim));
            dimTag.putInt("realWeatherTimer", realWeatherTickTimers.get(dim));
            dimTag.putLong("lastGameTick", lastGameTicks.get(dim));
            dimTag.putFloat("windIntensity", windIntensities.get(dim));

            atmospheres.get(dim).save(dimTag);
            histories.get(dim).save(dimTag);
            durationTrackers.get(dim).save(dimTag);

            tag.put("dim_" + dim.name(), dimTag);
        }
        return tag;
    }

    private static WeatherManager load(CompoundTag tag, HolderLookup.Provider provider) {
        WeatherManager m = new WeatherManager();
        for (WeatherTypes.Dimension dim : WeatherTypes.Dimension.values()) {
            String key = "dim_" + dim.name();
            if (tag.contains(key)) {
                CompoundTag dimTag = tag.getCompound(key);
                m.currentWeathers.put(dim, WeatherTypes.fromNameSafe(dimTag.getString("weatherType")));
                m.weatherTimers.put(dim, dimTag.getInt("weatherTicksRemaining"));
                m.realWeatherTickTimers.put(dim, dimTag.getInt("realWeatherTimer"));
                m.lastGameTicks.put(dim, dimTag.getLong("lastGameTick"));
                m.windIntensities.put(dim, dimTag.getFloat("windIntensity"));

                m.atmospheres.get(dim).load(dimTag);
                m.histories.get(dim).load(dimTag);
                m.durationTrackers.get(dim).load(dimTag);
            }
        }
        return m;
    }

    public static WeatherManager get(ServerLevel level) {
        WeatherManager manager = level.getDataStorage().computeIfAbsent(
                new Factory<>(WeatherManager::new, WeatherManager::load), NAME);

        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        WeatherTypes current = manager.currentWeathers.get(dim);

        if (!isCompatibleWithLevel(current, level)) {
            WeatherTypes fallback = defaultForDimension(dim);
            manager.currentWeathers.put(dim, fallback);
            manager.setDirty();
        }

        if (manager.weatherTimers.get(dim) < 0) {
            manager.startAutoWeather(level);
        }

        return manager;
    }
}