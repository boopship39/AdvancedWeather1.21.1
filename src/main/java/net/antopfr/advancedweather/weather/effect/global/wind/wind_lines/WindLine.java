package net.antopfr.advancedweather.weather.effect.global.wind.wind_lines;

import net.minecraft.world.phys.Vec3;

public class WindLine {
    public Vec3 start;
    public Vec3 direction;
    public float length;
    public float life;
    public float speed;
    public float opacity;

    public WindLine(Vec3 start, Vec3 direction, float length, float speed) {
        this.start = start;
        this.direction = direction;
        this.length = length;
        this.speed = speed;
        this.life = 0f;
        this.opacity = 0.05f + (float) Math.random() * 0.3f;
    }
}
