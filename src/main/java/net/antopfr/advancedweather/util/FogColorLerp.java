package net.antopfr.advancedweather.util;

public class FogColorLerp {

    private int colorFrom = 0xFFFFFF;
    private int colorTarget = 0xFFFFFF;
    private float progress = 1.0f;
    private final int durationTicks;

    public FogColorLerp(int durationTicks) {
        this.durationTicks = durationTicks;
    }

    public void tick() {
        if (progress < 1.0f) {
            progress = Math.min(1.0f, progress + (1.0f / durationTicks));
        }
    }

    public void setTarget(int newColor) {
        if (newColor == colorTarget) return;
        colorFrom = getCurrentColor();
        colorTarget = newColor;
        progress = 0.0f;
    }

    public void reset(int color) {
        colorFrom = color;
        colorTarget = color;
        progress = 1.0f;
    }

    public int getCurrentColor() {
        if (progress >= 1.0f) return colorTarget;
        int r1 = (colorFrom >> 16) & 0xFF, g1 = (colorFrom >> 8) & 0xFF, b1 = colorFrom & 0xFF;
        int r2 = (colorTarget >> 16) & 0xFF, g2 = (colorTarget >> 8) & 0xFF, b2 = colorTarget & 0xFF;
        int r = (int)(r1 + (r2 - r1) * progress);
        int g = (int)(g1 + (g2 - g1) * progress);
        int b = (int)(b1 + (b2 - b1) * progress);
        return (r << 16) | (g << 8) | b;
    }
}