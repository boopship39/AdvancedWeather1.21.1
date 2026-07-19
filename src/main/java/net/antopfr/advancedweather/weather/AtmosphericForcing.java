package net.antopfr.advancedweather.weather;

import net.minecraft.nbt.CompoundTag;

public class AtmosphericForcing {

    private final float pressurePushPerTick;
    private final float dewPointPushPerTick;
    private final float tempOffset;
    private int remainingTicks;
    private final int totalTicks;

    public AtmosphericForcing(float totalPressurePush, float totalDewPointPush,
                              float tempOffset, int durationTicks) {
        this.totalTicks = Math.max(1, durationTicks);
        this.pressurePushPerTick = totalPressurePush / this.totalTicks;
        this.dewPointPushPerTick = totalDewPointPush / this.totalTicks;
        this.tempOffset = tempOffset;
        this.remainingTicks = this.totalTicks;
    }

    private AtmosphericForcing(float pPerTick, float dpPerTick, float tempOffset,
                               int remaining, int total) {
        this.pressurePushPerTick = pPerTick;
        this.dewPointPushPerTick = dpPerTick;
        this.tempOffset = tempOffset;
        this.remainingTicks = remaining;
        this.totalTicks = total;
    }

    public boolean tick() {
        remainingTicks--;
        return remainingTicks > 0;
    }

    public float pressurePushPerTick() { return pressurePushPerTick; }
    public float dewPointPushPerTick() { return dewPointPushPerTick; }

    public float currentTempOffset() {
        float fadeStart = totalTicks * 0.2f;
        if (remainingTicks < fadeStart) {
            return tempOffset * (remainingTicks / fadeStart);
        }
        return tempOffset;
    }

    public int remainingTicks() { return remainingTicks; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("pPush", pressurePushPerTick);
        tag.putFloat("dpPush", dewPointPushPerTick);
        tag.putFloat("tempOff", tempOffset);
        tag.putInt("remaining", remainingTicks);
        tag.putInt("total", totalTicks);
        return tag;
    }

    public static AtmosphericForcing load(CompoundTag tag) {
        return new AtmosphericForcing(
                tag.getFloat("pPush"), tag.getFloat("dpPush"), tag.getFloat("tempOff"),
                tag.getInt("remaining"), tag.getInt("total"));
    }
}
