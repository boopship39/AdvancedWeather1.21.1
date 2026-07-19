package net.antopfr.advancedweather.compat.sereneseasons;

import net.antopfr.advancedweather.util.ModLoaded;
import net.minecraft.server.level.ServerLevel;

public class SeasonCompat {
    public static int getCurrentSeasonOrdinal(ServerLevel level) {
        if (!ModLoaded.isSereneSeasonsLoaded()) return -1;
        try {
            return SeasonCompatImpl.getSeasonOrdinal(level);
        } catch (Throwable t) {
            return -1;
        }
    }
}
