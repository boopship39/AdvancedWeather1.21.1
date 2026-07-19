package net.antopfr.advancedweather.util;

import net.neoforged.fml.ModList;

public class ModLoaded {
    public static boolean CREATE = modLoaded("create");
    public static boolean SERENESEASONS = modLoaded("sereneseasons");
    public static boolean SIMULATED = modLoaded("simulated");

    public static boolean isCreateLoaded() {
        return CREATE;
    }
    public static boolean isSereneSeasonsLoaded() {
        return SERENESEASONS;
    }
    public static boolean isSimulatedLoaded() {
        return SIMULATED;
    }

    private static boolean modLoaded(String mod) {
        return (ModList.get().isLoaded(mod));
    }

    public static void logAllCompat() {
        if (ModLoaded.isCreateLoaded()) {
            AWLogger.AW.info("[AdvancedWeather] Create detected, compatibility enabled");
        }
        if (ModLoaded.isSereneSeasonsLoaded()) {
            AWLogger.AW.info("[AdvancedWeather] Serene Seasons detected, compatibility enabled");
        }
        if (ModLoaded.isSimulatedLoaded()) {
            AWLogger.AW.info("[AdvancedWeather] Simulated detected, compatibility enabled");
        }
    }
}
