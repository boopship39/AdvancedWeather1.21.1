package net.antopfr.advancedweather.content.block.archive;

import net.minecraft.client.Minecraft;

public class WeatherArchiveScreenOpener {
    public static void open(WeatherArchiveBlockEntity archive) {
        Minecraft.getInstance().setScreen(new WeatherArchiveScreen(archive));
    }
}
