package net.antopfr.advancedweather.weather.effect.global.wind;

import net.minecraft.world.phys.Vec3;

public class WindDirectionCalc {

    private static final double DIRECTION_DRIFT = 0.00004;
    private static final double GUST_SPEED      = 0.0011;
    private static final double GUST_ANGLE_SWAY = 0.35;

    public static Vec3 getDirection(long dayTime, float partialTick) {
        double t = dayTime + partialTick;

        double baseAngle = smoothNoise(t * DIRECTION_DRIFT, 0.0) * Math.PI * 2.0;
        double sway = smoothNoise(t * GUST_SPEED, 500.0) * GUST_ANGLE_SWAY;

        double angle = baseAngle + sway;
        return new Vec3(Math.cos(angle), 0, Math.sin(angle));
    }

    private static double smoothNoise(double t, double offset) {
        double raw = Math.sin(t + offset)
                + Math.sin(t * 2.3 + offset * 1.7) * 0.5
                + Math.sin(t * 5.1 + offset * 0.9) * 0.25;
        return raw / 1.75;
    }
}