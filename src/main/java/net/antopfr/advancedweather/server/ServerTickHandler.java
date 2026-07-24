package net.antopfr.advancedweather.server;

import net.antopfr.advancedweather.api.external.OpenMeteo;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.advancement.AWAdvancementEvents;
import net.antopfr.advancedweather.network.toclient.*;
import net.antopfr.advancedweather.weather.effect.EffectManager;
import net.antopfr.advancedweather.weather.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = "advancedweather")
public class ServerTickHandler {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        WeatherManager.get(level).tick(level);
    }

    @SubscribeEvent
    public static void onVanillaWeatherDisable(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.getLevelData() instanceof ServerLevelData serverData) {

                if (serverData.isRaining()) {
                    serverData.setRaining(false);
                }
                if (serverData.isThundering()) {
                    serverData.setThundering(false);
                }

                serverData.setRainTime(24000);
                serverData.setThunderTime(24000);
                serverData.setClearWeatherTime(24000);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        AWCommonConfig config = AWCommonConfig.get();
        WeatherManager manager = WeatherManager.get(level);

        if (config.realWeatherEnabled && level.dimension().equals(Level.OVERWORLD)) {
            OpenMeteo.fetchAsync(config.latitude, config.longitude).thenAccept(type -> {
                if (type == null) return;
                level.getServer().execute(() -> manager.applyRealWeather(level, type));
            });
        } else {
            manager.startAutoWeather(level);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncAllToPlayer(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncAllToPlayer(player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncAllToPlayer(player);
    }

    private static void syncAllToPlayer(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        WeatherManager manager = WeatherManager.get(level);
        WeatherTypes.Dimension dim = DimensionProfile.getDimension(level);
        AtmosphericSystem a = manager.getAtmosphere(level);
        WeatherTypes current = manager.getCurrentWeather(level);
        long time = level.getGameTime();

        AWAdvancementEvents.weatherExperienced(player, current);

        AtmosphericSystem forecast = a.forecastState(30, time, level);
        List<WeatherTypes> history = manager.getRecentHistory(level);

        WeatherTransitionGraph.Prediction predictionNext =
                WeatherTransitionGraph.mostLikelyNextWithWeights(current, a, dim, level, history);
        WeatherTransitionGraph.Prediction predictionIn30 =
                WeatherTransitionGraph.mostLikelyNextWithWeights(current, forecast, dim, level, history);

        float confidenceNext = ForecastConfidence.compute(
                predictionNext.transitions(), predictionNext.weights(), a, 0);
        float confidenceIn30 = ForecastConfidence.compute(
                predictionIn30.transitions(), predictionIn30.weights(), forecast, 30);

        WeatherTypes predictedNext = predictionNext.type();
        WeatherTypes predictedIn30 = predictionIn30.type();

        PacketDistributor.sendToPlayer(player, new WeatherSyncPacket(current));
        PacketDistributor.sendToPlayer(player, new EffectSyncPacket(EffectManager.get(level).getActiveEffects()));
        PacketDistributor.sendToPlayer(player, new HistorySyncPacket(manager.getHistory(level).getEntries()));
        PacketDistributor.sendToPlayer(player, new PressureSyncPacket(
                a.getPressure(), a.getPressureVel(), forecast.getPressure(),
                a.getCategory(level).name(),
                predictedNext.name(), predictedIn30.name(),
                a.getWindIntensity(), a.getMode().name(),
                confidenceNext, confidenceIn30));
        PacketDistributor.sendToPlayer(player, new TemperatureSyncPacket(
                a.getTemperature(), a.forecastTemperatureFromState(forecast, 30, time, current, level)));
        PacketDistributor.sendToPlayer(player, new HumiditySyncPacket(
                a.getHumidity(), a.forecastHumidityFromState(forecast, current, 30, time, level)));
    }
}
