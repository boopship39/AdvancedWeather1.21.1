package net.antopfr.advancedweather.content.item.almanac;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class WeatherAlmanacScreenOpener {
    public static void open(ItemStack almanac) {
       Minecraft.getInstance().setScreen(new WeatherAlmanacScreen(almanac));
    }
}
