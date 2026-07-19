package net.antopfr.advancedweather.util;

import net.minecraft.util.Mth;

public class ValueColors {

    public static int temperature(float temp) {
        int r, g, b;
        if (temp <= -20f) {
            float factor = Mth.clamp((temp - (-60f)) / 40f, 0f, 1f);
            r = (int) Mth.lerp(factor, 25, 40);
            g = (int) Mth.lerp(factor, 5, 100);
            b = (int) Mth.lerp(factor, 50, 220);
        } else if (temp <= 0f) {
            float factor = Mth.clamp((temp - (-20f)) / 20f, 0f, 1f);
            r = (int) Mth.lerp(factor, 40, 180);
            g = (int) Mth.lerp(factor, 100, 230);
            b = (int) Mth.lerp(factor, 220, 255);
        } else if (temp <= 18f) {
            float factor = Mth.clamp((temp - 0f) / 18f, 0f, 1f);
            r = (int) Mth.lerp(factor, 180, 235);
            g = (int) Mth.lerp(factor, 230, 215);
            b = (int) Mth.lerp(factor, 255, 150);
        } else if (temp <= 35f) {
            float factor = Mth.clamp((temp - 18f) / 17f, 0f, 1f);
            r = (int) Mth.lerp(factor, 235, 245);
            g = (int) Mth.lerp(factor, 215, 120);
            b = (int) Mth.lerp(factor, 150, 30);
        } else if (temp <= 65f) {
            float factor = Mth.clamp((temp - 35f) / 30f, 0f, 1f);
            r = (int) Mth.lerp(factor, 245, 220);
            g = (int) Mth.lerp(factor, 120, 0);
            b = (int) Mth.lerp(factor, 30, 0);
        } else {
            float factor = Mth.clamp((temp - 65f) / 55f, 0f, 1f);
            r = (int) Mth.lerp(factor, 220, 0);
            g = (int) Mth.lerp(factor, 0, 0);
            b = (int) Mth.lerp(factor, 0, 5);
        }
        return (r << 16) | (g << 8) | b;
    }

    public static int wind(float speedKmh) {
        int r, g, b;
        if (speedKmh <= 15f) {
            float factor = Mth.clamp(speedKmh / 15f, 0f, 1f);
            r = (int) Mth.lerp(factor, 220, 115);
            g = (int) Mth.lerp(factor, 225, 215);
            b = (int) Mth.lerp(factor, 230, 125);
        } else if (speedKmh <= 40f) {
            float factor = Mth.clamp((speedKmh - 15f) / 25f, 0f, 1f);
            r = (int) Mth.lerp(factor, 115, 235);
            g = (int) Mth.lerp(factor, 215, 185);
            b = (int) Mth.lerp(factor, 125, 55);
        } else if (speedKmh <= 75f) {
            float factor = Mth.clamp((speedKmh - 40f) / 35f, 0f, 1f);
            r = (int) Mth.lerp(factor, 235, 230);
            g = (int) Mth.lerp(factor, 185, 50);
            b = (int) Mth.lerp(factor, 55, 35);
        } else {
            float factor = Mth.clamp((speedKmh - 75f) / 45f, 0f, 1f);
            r = (int) Mth.lerp(factor, 230, 120);
            g = (int) Mth.lerp(factor, 50, 10);
            b = (int) Mth.lerp(factor, 35, 160);
        }
        return (r << 16) | (g << 8) | b;
    }

    public static int humidity(float hum) {
        int r, g, b;
        if (hum <= 20f) {
            float factor = Mth.clamp(hum / 20f, 0f, 1f);
            r = (int) Mth.lerp(factor, 185, 140);
            g = (int) Mth.lerp(factor, 135, 180);
            b = (int) Mth.lerp(factor, 80, 100);
        } else if (hum <= 50f) {
            float factor = Mth.clamp((hum - 20f) / 30f, 0f, 1f);
            r = (int) Mth.lerp(factor, 140, 70);
            g = (int) Mth.lerp(factor, 180, 190);
            b = (int) Mth.lerp(factor, 100, 210);
        } else if (hum <= 80f) {
            float factor = Mth.clamp((hum - 50f) / 30f, 0f, 1f);
            r = (int) Mth.lerp(factor, 70, 30);
            g = (int) Mth.lerp(factor, 190, 110);
            b = (int) Mth.lerp(factor, 110, 230);
        } else {
            float factor = Mth.clamp((hum - 80f) / 20f, 0f, 1f);
            r = (int) Mth.lerp(factor, 30, 10);
            g = (int) Mth.lerp(factor, 110, 30);
            b = (int) Mth.lerp(factor, 230, 150);
        }
        return (r << 16) | (g << 8) | b;
    }

    public static String humidityLabel(float hum) {
        return hum < 20f ? "Dry" : hum < 40f ? "Mild" : hum < 70f ? "Humid" : "Saturated";
    }
}
