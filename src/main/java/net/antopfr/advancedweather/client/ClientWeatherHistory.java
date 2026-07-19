package net.antopfr.advancedweather.client;

import net.antopfr.advancedweather.weather.WeatherHistory;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClientWeatherHistory {

    private static List<WeatherHistory.Entry> entries = Collections.emptyList();

    public static void set(List<WeatherHistory.Entry> e) { entries = e; }
    public static List<WeatherHistory.Entry> get()       { return entries; }

    public static void append(List<WeatherHistory.Entry> newEntries) {
        List<WeatherHistory.Entry> merged = new ArrayList<>(entries);

        for (WeatherHistory.Entry newEntry : newEntries) {
            boolean exists = merged.stream().anyMatch(e ->
                    e.gameTick() == newEntry.gameTick() && e.dimension() == newEntry.dimension()
            );
            if (!exists) {
                merged.add(newEntry);
            }
        }

        merged.sort(Comparator.comparingLong(WeatherHistory.Entry::gameTick));

        if (merged.size() > 1620) {
            merged = merged.subList(merged.size() - 1620, merged.size());
        }

        entries = merged;
    }

    public static List<WeatherHistory.Entry> getLastMinutes(int inGameMinutes) {
        if (entries.isEmpty()) return entries;
        int ticks = inGameMinutes * 1200;
        long currentTick = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getGameTime() : 0;
        long cutoff = currentTick - ticks;
        return entries.stream()
                .filter(e -> e.gameTick() >= cutoff)
                .toList();
    }

    public static void clear() { entries = Collections.emptyList(); }
}