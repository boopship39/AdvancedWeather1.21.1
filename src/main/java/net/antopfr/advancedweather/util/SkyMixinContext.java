package net.antopfr.advancedweather.util;

import net.minecraft.world.phys.Vec3;

public class SkyMixinContext {

    private static Vec3 vanillaSkyColor = new Vec3(0.6, 0.7, 1.0);

    public static void setVanillaSkyColor(Vec3 color) {
        vanillaSkyColor = color;
    }

    public static Vec3 vanillaSkyColor() {
        return vanillaSkyColor;
    }
}
