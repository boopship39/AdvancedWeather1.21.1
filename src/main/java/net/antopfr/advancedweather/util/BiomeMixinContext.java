package net.antopfr.advancedweather.util;

import net.minecraft.server.level.ServerLevel;

public class BiomeMixinContext {

    private static final ThreadLocal<ServerLevel> CURRENT_LEVEL = new ThreadLocal<>();

    public static void setCurrentLevel(ServerLevel level) {
        CURRENT_LEVEL.set(level);
    }

    public static void clearCurrentLevel() {
        CURRENT_LEVEL.remove();
    }

    public static ServerLevel getCurrentLevel() {
        return CURRENT_LEVEL.get();
    }
}
