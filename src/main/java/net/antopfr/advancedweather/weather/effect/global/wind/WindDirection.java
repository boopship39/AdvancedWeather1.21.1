package net.antopfr.advancedweather.weather.effect.global.wind;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WindDirection {

    public static Vec3 get(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return new Vec3(1, 0, 0);
        return WindDirectionCalc.getDirection(mc.level.getDayTime(), partialTick);
    }

    public static Vec3 getHorizontal(Level level) {
        return WindDirectionCalc.getDirection(level.getDayTime(), 0f);
    }

    public static float getIntensityClient() {
        return ClientAtmosphereState.getWindIntensity();
    }

    public static float getIntensity(ServerLevel level) {
        return WeatherManager.get(level).getAtmosphere(level).getWindIntensity();
    }
}
