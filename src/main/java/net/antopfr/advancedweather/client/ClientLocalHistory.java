package net.antopfr.advancedweather.client;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherHistory;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientLocalHistory {

    public record LocalEntry(long gameTick, float localTemperature, float localHumidity, float localPressure, float localWind, WeatherTypes weather) {}

    private static List<LocalEntry> entries = Collections.emptyList();
    private static long lastLoggedTick = -1;

    public static void tick(long gameTick) {
        if (lastLoggedTick != -1 && gameTick < lastLoggedTick) {
            lastLoggedTick = gameTick;
            return;
        }

        if (lastLoggedTick == -1 || gameTick - lastLoggedTick >= WeatherHistory.SAMPLE_INTERVAL) {
            lastLoggedTick = gameTick;

            List<LocalEntry> merged = new ArrayList<>(entries);

            merged.add(new LocalEntry(
                    gameTick,
                    ClientAtmosphereState.getLocalTemperature(),
                    ClientAtmosphereState.getLocalHumidity(),
                    ClientAtmosphereState.getLocalPressure(),
                    ClientAtmosphereState.getWindIntensity(),
                    ClientWeatherState.getCurrentWeather()
            ));

            if (merged.size() > 540) merged = merged.subList(merged.size() - 540, merged.size());
            entries = merged;
        }
    }

    public static List<LocalEntry> getLastMinutes(int inGameMinutes) {
        if (entries.isEmpty()) return entries;
        int ticks = inGameMinutes * 1200;

        long currentTick = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getGameTime() : 0;
        long cutoff = currentTick - ticks;

        return entries.stream().filter(e -> e.gameTick() >= cutoff).toList();
    }

    public static void clear() {
        entries = Collections.emptyList();
        lastLoggedTick = -1;
    }

    public static void setEntries(List<LocalEntry> newEntries) {
        entries = new ArrayList<>(newEntries);

        if (!entries.isEmpty()) {
            lastLoggedTick = entries.getLast().gameTick();
        }
    }

    public static boolean isEmpty() {
        return entries.isEmpty();
    }

    public static void resetForDimensionChange() {
    }
}
