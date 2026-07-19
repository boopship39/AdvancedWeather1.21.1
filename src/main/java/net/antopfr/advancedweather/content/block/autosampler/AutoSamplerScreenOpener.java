package net.antopfr.advancedweather.content.block.autosampler;

import net.minecraft.client.Minecraft;

public class AutoSamplerScreenOpener {
    public static void open(AutoSamplerBlockEntity be) {
        Minecraft.getInstance().setScreen(new AutoSamplerScreen(be));
    }
}
