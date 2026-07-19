package net.antopfr.advancedweather.weather;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class WeatherHistory {

    public record Entry(long gameTick, float pressure, float wind,
                        float temperature, float humidity, WeatherTypes weather,
                        WeatherTypes.Dimension dimension) {}

    public static final int SAMPLE_INTERVAL = 80;
    private static final int MAX_ENTRIES = 900;

    private final Deque<Entry> entries = new ArrayDeque<>();

    public void tick(long gameTick, float pressure, float wind,
                     float temperature, float humidity, WeatherTypes weather,
                     WeatherTypes.Dimension dimension) {
        if (gameTick % SAMPLE_INTERVAL != 0) return;
        entries.addLast(new Entry(gameTick, pressure, wind, temperature, humidity, weather, dimension));
        if (entries.size() > MAX_ENTRIES) entries.pollFirst();
    }

    public List<Entry> getEntries() { return new ArrayList<>(entries); }

    public List<Entry> getLastMinutes(int inGameMinutes) {
        int ticks = inGameMinutes * 1200;
        long cutoff = entries.isEmpty() ? 0
                : entries.peekLast().gameTick() - ticks;
        return entries.stream()
                .filter(e -> e.gameTick() >= cutoff)
                .toList();
    }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Entry e : entries) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("tick",         e.gameTick());
            entry.putFloat("pressure",    e.pressure());
            entry.putFloat("wind",        e.wind());
            entry.putFloat("temperature", e.temperature());
            entry.putFloat("humidity",    e.humidity());
            entry.putString("weather",    e.weather().name());
            entry.putString("dimension",  e.dimension().name());
            list.add(entry);
        }
        tag.put("weatherHistory", list);
    }
    public void load(@NotNull CompoundTag tag) {
        entries.clear();
        ListTag list = tag.getList("weatherHistory", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            entries.addLast(new Entry(
                    entry.getLong("tick"),
                    entry.getFloat("pressure"),
                    entry.getFloat("wind"),
                    entry.contains("temperature") ? entry.getFloat("temperature") : 15.0f,
                    entry.contains("humidity") ? entry.getFloat("humidity") : 60.0f,
                    WeatherTypes.fromNameSafe(entry.getString("weather")),
                    WeatherTypes.Dimension.valueOf(entry.getString("dimension"))
            ));
        }
    }
}