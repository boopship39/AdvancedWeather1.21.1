package net.antopfr.advancedweather.weather;

import net.minecraft.nbt.CompoundTag;

public class AtmosphericForcing {

    public enum Bias {
        NONE, SEEDING, DISSIPATING, COOLING, HEATING
    }

    private final float pressurePushPerTick;
    private final float dewPointPushPerTick;
    private final float tempOffset;
    private final Bias bias;
    private int remainingTicks;
    private final int totalTicks;

    public AtmosphericForcing(float totalPressurePush, float totalDewPointPush,
                              float tempOffset, int durationTicks, Bias bias) {
        this.totalTicks = Math.max(1, durationTicks);
        this.pressurePushPerTick = totalPressurePush / this.totalTicks;
        this.dewPointPushPerTick = totalDewPointPush / this.totalTicks;
        this.tempOffset = tempOffset;
        this.bias = bias != null ? bias : Bias.NONE;
        this.remainingTicks = this.totalTicks;
    }

    private AtmosphericForcing(float pPerTick, float dpPerTick, float tempOffset,
                               Bias bias, int remaining, int total) {
        this.pressurePushPerTick = pPerTick;
        this.dewPointPushPerTick = dpPerTick;
        this.tempOffset = tempOffset;
        this.bias = bias;
        this.remainingTicks = remaining;
        this.totalTicks = total;
    }

    public boolean tick() {
        remainingTicks--;
        return remainingTicks > 0;
    }

    public float pressurePushPerTick() { return pressurePushPerTick; }
    public float dewPointPushPerTick() { return dewPointPushPerTick; }
    public Bias bias() { return bias; }

    public float currentTempOffset() {
        float fadeStart = totalTicks * 0.2f;
        if (remainingTicks < fadeStart) {
            return tempOffset * (remainingTicks / fadeStart);
        }
        return tempOffset;
    }

    public static float biasModifier(WeatherTypes target, AtmosphericForcing.Bias bias) {
        return switch (bias) {
            case SEEDING -> switch (target) {
                case LIGHT_RAIN, HEAVY_RAIN, DRIZZLE, THUNDERSTORM, OVERCAST -> 2.2f;
                case CLEAR, SUNNY -> 0.4f;
                default -> 1f;
            };
            case DISSIPATING -> switch (target) {
                case CLEAR, SUNNY -> 2.2f;
                case LIGHT_RAIN, HEAVY_RAIN, THUNDERSTORM, OVERCAST, DRIZZLE -> 0.4f;
                default -> 1f;
            };
            case COOLING -> switch (target) {
                case SNOW, FREEZING_RAIN, BLIZZARD -> 24.0f;
                default -> 1f;
            };
            case HEATING -> switch (target) {
                case SUNNY, CLEAR -> 4.3f;
                case SNOW, BLIZZARD, FREEZING_RAIN -> 0.3f;
                default -> 1f;
            };
            case NONE -> 1f;
        };
    }

    public int remainingTicks() { return remainingTicks; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("pPush", pressurePushPerTick);
        tag.putFloat("dpPush", dewPointPushPerTick);
        tag.putFloat("tempOff", tempOffset);
        tag.putString("bias", bias.name());
        tag.putInt("remaining", remainingTicks);
        tag.putInt("total", totalTicks);
        return tag;
    }

    public static AtmosphericForcing load(CompoundTag tag) {
        Bias bias = Bias.NONE;
        if (tag.contains("bias")) {
            try { bias = Bias.valueOf(tag.getString("bias")); }
            catch (IllegalArgumentException ignored) {}
        }
        return new AtmosphericForcing(
                tag.getFloat("pPush"), tag.getFloat("dpPush"), tag.getFloat("tempOff"),
                bias, tag.getInt("remaining"), tag.getInt("total"));
    }
}