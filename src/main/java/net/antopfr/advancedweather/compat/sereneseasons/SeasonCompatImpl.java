package net.antopfr.advancedweather.compat.sereneseasons;

import net.minecraft.server.level.ServerLevel;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

class SeasonCompatImpl {
    static int getSeasonOrdinal(ServerLevel level) {
        ISeasonState state = SeasonHelper.getSeasonState(level);
        return state.getSeason().ordinal();
    }
}
