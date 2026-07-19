package net.antopfr.advancedweather.weather;

public enum WeatherTypes {

    // OVERWORLD
    CLEAR("Clear", false, false, Dimension.OVERWORLD),
    SUNNY("Sunny", false, false, Dimension.OVERWORLD),
    CLOUDY("Cloudy", false, false, Dimension.OVERWORLD),
    OVERCAST("Overcast", false, false, Dimension.OVERWORLD),
    MIST("Mist", false, false, Dimension.OVERWORLD),
    DRIZZLE("Drizzle", true, false, Dimension.OVERWORLD),
    LIGHT_RAIN("Light Rain", true, false, Dimension.OVERWORLD),
    HEAVY_RAIN("Heavy Rain", true, false, Dimension.OVERWORLD),
    FREEZING_RAIN("Freezing Rain", true, false, Dimension.OVERWORLD),
    THUNDERSTORM("Thunderstorm", true, true, Dimension.OVERWORLD),
    SNOW("Snow", true, false, Dimension.OVERWORLD),
    BLIZZARD("Blizzard", true, false, Dimension.OVERWORLD),
    HAIL("Hail", true, false, Dimension.OVERWORLD),
    FOG("Fog", false, false, Dimension.OVERWORLD),
    DENSE_FOG("Dense Fog", false, false, Dimension.OVERWORLD),
    WINDY("Windy", false, false, Dimension.OVERWORLD),
    SANDSTORM("Sandstorm", false, false, Dimension.OVERWORLD),

    // NETHER
    NETHER_CLEAR("Nether Clear", false, false, Dimension.NETHER),
    BRIMSTONE_STORM("Brimstone Storm", false, false, Dimension.NETHER),
    LAVA_RAIN("Lava Rain", false, false, Dimension.NETHER),
    ASH_STORM("Ash Storm", false, false, Dimension.NETHER),
    NETHERSTORM("Netherstorm", false, true, Dimension.NETHER),
    HELLFIRE("Hellfire", false, false, Dimension.NETHER),

    // END
    END_CLEAR("End Clear", false, false, Dimension.END),
    VOID_STORM("Void Storm", false, false, Dimension.END),
    END_MIST("End Mist", false, false, Dimension.END),
    CHORUS_GALE("Chorus Gale", false, false, Dimension.END),
    ENDERSTORM("Enderstorm", false, true, Dimension.END);

    public enum Dimension { OVERWORLD, NETHER, END }

    private final String    weatherName;
    private final boolean   vanillaRaining;
    private final boolean   vanillaThundering;
    public  final Dimension dimension;

    WeatherTypes(String name, boolean rain, boolean thunder, Dimension dim) {
        this.weatherName      = name;
        this.vanillaRaining   = rain;
        this.vanillaThundering = thunder;
        this.dimension        = dim;
    }

    public String  weatherName()        { return weatherName; }
    public boolean isVanillaRaining()   { return vanillaRaining; }
    public boolean isVanillaThundering(){ return vanillaThundering; }
    public Dimension dimension()        { return dimension; }

    public boolean isOverworld() { return dimension == Dimension.OVERWORLD; }
    public boolean isNether()    { return dimension == Dimension.NETHER; }
    public boolean isEnd()       { return dimension == Dimension.END; }

    public boolean hasFog() {
        return this == FOG || this == DENSE_FOG || this == BLIZZARD
                || this == FREEZING_RAIN || this == THUNDERSTORM
                || this == SANDSTORM || this == HAIL
                || this == BRIMSTONE_STORM || this == ASH_STORM || this == NETHERSTORM || this == HELLFIRE
                || this == END_MIST || this == ENDERSTORM || this == VOID_STORM;
    }

    public static WeatherTypes fromNameSafe(String name) {
        if (name == null) return CLEAR;
        try { return valueOf(name.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return CLEAR; }
    }
}
