package net.antopfr.advancedweather.weather.effect.global.wind;

import net.minecraft.world.phys.Vec3;

public class WindDirectionCalc {
    private static final double NOISE_OFFSET_X = 1000.0;
    private static final double NOISE_OFFSET_Z = 5000.0;
    private static final double WIND_EVOLUTION_SPEED = 0.00005;
    private static final double TURBULENCE_SPEED = 0.0003;
    private static final double TURBULENCE_STRENGTH = 0.3;

    public static Vec3 get(long dayTime, float partialTick) {
        double t = dayTime + partialTick;

        double baseAngle = sampleNoise(t * WIND_EVOLUTION_SPEED, NOISE_OFFSET_X) * Math.PI * 2;
        double turbulenceX = sampleNoise(t * TURBULENCE_SPEED, NOISE_OFFSET_X + 100) * TURBULENCE_STRENGTH;
        double turbulenceZ = sampleNoise(t * TURBULENCE_SPEED, NOISE_OFFSET_Z + 100) * TURBULENCE_STRENGTH;

        double windX = Math.cos(baseAngle) + turbulenceX;
        double windZ = Math.sin(baseAngle) + turbulenceZ;

        return new Vec3(windX, 0, windZ).normalize();
    }

    private static double sampleNoise(double t, double offset) {
        return Math.sin(t + offset)
                + Math.sin(t * 2.3 + offset * 1.7) * 0.5
                + Math.sin(t * 5.1 + offset * 0.9) * 0.25
                + Math.sin(t * 11.7 + offset * 2.3) * 0.125;
    }
}
