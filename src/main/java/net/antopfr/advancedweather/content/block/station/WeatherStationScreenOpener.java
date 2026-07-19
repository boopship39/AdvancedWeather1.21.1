package net.antopfr.advancedweather.content.block.station;

import net.minecraft.client.Minecraft;

public class WeatherStationScreenOpener {
    public static void open(WeatherStationBlockEntity station) {
        Minecraft.getInstance().setScreen(
                new WeatherStationScreen(station));
    }
}
