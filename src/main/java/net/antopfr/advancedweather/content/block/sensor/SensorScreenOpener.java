package net.antopfr.advancedweather.content.block.sensor;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SensorScreenOpener {
    public static <T extends BlockEntity & IWeatherSensor> void open(T be) {
        Minecraft.getInstance().setScreen(new SensorScreen(be));
    }
}
