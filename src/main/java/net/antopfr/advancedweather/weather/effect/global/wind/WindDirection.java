package net.antopfr.advancedweather.weather.effect.global.wind;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class WindDirection {
    public static Vec3 get(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return new Vec3(1, 0, 0);
        return WindDirectionCalc.get(mc.level.getDayTime(), partialTick);
    }
}
