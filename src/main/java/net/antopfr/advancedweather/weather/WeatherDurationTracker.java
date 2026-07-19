package net.antopfr.advancedweather.weather;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class WeatherDurationTracker {

    private final Map<WeatherTypes, Integer> consecutiveTicks = new EnumMap<>(WeatherTypes.class);
    private WeatherTypes lastType = null;

    public void tick(WeatherTypes current) {
        if (current != lastType) {
            if (lastType != null) consecutiveTicks.put(lastType, 0);
            lastType = current;
        }
        consecutiveTicks.merge(current, 1, Integer::sum);
    }

    public int getConsecutiveTicks(WeatherTypes type) {
        return consecutiveTicks.getOrDefault(type, 0);
    }

    public int getConsecutiveSeconds(WeatherTypes type) {
        return getConsecutiveTicks(type) / 20;
    }

    public int getConsecutiveMinutes(WeatherTypes type) {
        return getConsecutiveTicks(type) / 1200;
    }

    public boolean hasLastedTicks(WeatherTypes type, int ticks) {
        return getConsecutiveTicks(type) >= ticks;
    }

    public boolean hasLastedSeconds(WeatherTypes type, int seconds) {
        return hasLastedTicks(type, seconds * 20);
    }

    public boolean hasLastedMinutes(WeatherTypes type, int minutes) {
        return hasLastedTicks(type, minutes * 1200);
    }

    public void save(CompoundTag tag) {
        CompoundTag inner = new CompoundTag();
        consecutiveTicks.forEach((type, ticks) ->
                inner.putInt(type.name(), ticks));
        inner.putString("lastType", lastType != null ? lastType.name() : "NONE");
        tag.put("weatherDuration", inner);
    }
    public void load(CompoundTag tag) {
        if (!tag.contains("weatherDuration")) return;
        CompoundTag inner = tag.getCompound("weatherDuration");
        for (WeatherTypes type : WeatherTypes.values()) {
            if (inner.contains(type.name())) {
                consecutiveTicks.put(type, inner.getInt(type.name()));
            }
        }
        String last = inner.getString("lastType");
        lastType = last.equals("NONE") ? null : WeatherTypes.fromNameSafe(last);
    }
}
