package net.antopfr.advancedweather.content.block.detector;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class WeatherDetectorScreenOpener {
    public static void open(BlockPos pos, WeatherDetectorBlock.DetectionMode mode) {
        Minecraft.getInstance().setScreen(new WeatherDetectorScreen(pos, mode));
    }
}
