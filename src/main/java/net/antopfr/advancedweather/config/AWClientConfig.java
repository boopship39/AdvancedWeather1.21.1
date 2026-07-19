package net.antopfr.advancedweather.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.antopfr.advancedweather.client.gui.hud.WeatherHudOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;

public class AWClientConfig {
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("advancedweather.json");

    private static final ConfigClassHandler<AWClientConfig> HANDLER = ConfigClassHandler.createBuilder(AWClientConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("advancedweather", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(PATH)
                    .build())
            .build();

    // weather hud
    @SerialEntry public boolean enableWeatherHud = true;
    @SerialEntry public boolean weatherHudShowTime = true;
    @SerialEntry public WeatherHudOverlay.HudPosition weatherHudPosition = WeatherHudOverlay.HudPosition.TOP_RIGHT;

    // units
    @SerialEntry public boolean useFahrenheit = false;
    @SerialEntry public boolean useMph = false;

    // wind lines
    @SerialEntry public boolean windBurstEnabled = true;
    @SerialEntry public int windLineMax = 80;
    @SerialEntry public double windLineSpeedMin = 0.4;
    @SerialEntry public double windLineSpeedMax = 0.6;
    @SerialEntry public double windLineLengthMin = 1.5;
    @SerialEntry public double windLineLengthMax = 4.5;
    @SerialEntry public double windLineSpawnDistMin = 15.0;
    @SerialEntry public double windLineSpawnDistMax = 25.0;
    @SerialEntry public double windLineLateralSpread = 35.0;
    @SerialEntry public double windLineHeightSpread = 9.0;
    @SerialEntry public int windBurstCooldownMin = 60;
    @SerialEntry public int windBurstCooldownMax = 120;
    @SerialEntry public int windBurstDurationMin = 60;  // 3s
    @SerialEntry public int windBurstDurationMax = 200; // 10s

    // wind on particles
    @SerialEntry public boolean windPushesParticles = true;
    @SerialEntry public double windParticleStrength = 1.0;

    // blizzard
    @SerialEntry public int blizzardCount = 48;
    @SerialEntry public int blizzardTickInterval = 2;
    @SerialEntry public double blizzardSpawnDist = 8.0;
    @SerialEntry public double blizzardSpread = 16.0;
    @SerialEntry public double blizzardHeightSpread = 12.0;
    @SerialEntry public double blizzardWindSpeed = 0.5;
    @SerialEntry public double blizzardSizeMin = 0.14;
    @SerialEntry public double blizzardSizeMax = 0.25;

    // sand
    @SerialEntry public int sandCount = 48;
    @SerialEntry public int sandTickInterval = 2;
    @SerialEntry public double sandSpawnDist = 8.0;
    @SerialEntry public double sandSpread = 16.0;
    @SerialEntry public double sandHeightSpread = 8.0;
    @SerialEntry public double sandWindSpeed = 0.8;
    @SerialEntry public double sandSizeMin = 0.22;
    @SerialEntry public double sandSizeMax = 0.27;

    // brimstone
    @SerialEntry public int brimstoneCount = 48;
    @SerialEntry public int brimstoneTickInterval = 2;
    @SerialEntry public double brimstoneSpawnDist = 8.0;
    @SerialEntry public double brimstoneSpread = 16.0;
    @SerialEntry public double brimstoneHeightSpread = 8.0;
    @SerialEntry public double brimstoneWindSpeed = 0.8;
    @SerialEntry public double brimstoneSizeMin = 0.22;
    @SerialEntry public double brimstoneSizeMax = 0.27;

    // fog wisps
    @SerialEntry public int fogWispCount = 8;
    @SerialEntry public int fogWispTickInterval = 6;
    @SerialEntry public double fogWispSpread = 24.0;
    @SerialEntry public double fogWispHeightSpread = 6.0;
    @SerialEntry public double fogWispAlphaMin = 0.3;
    @SerialEntry public double fogWispAlphaMax = 0.4;
    @SerialEntry public double fogWispSizeMin = 1.2;
    @SerialEntry public double fogWispSizeMax = 2.2;
    @SerialEntry public int fogWispLifetimeMin = 200;
    @SerialEntry public int fogWispLifetimeMax = 300;

    // ground fog
    @SerialEntry public int groundFogCount = 8;
    @SerialEntry public int groundFogTickInterval = 3;
    @SerialEntry public double groundFogSpread = 24.0;
    @SerialEntry public double groundFogAlphaMin = 0.1;
    @SerialEntry public double groundFogAlphaMax = 0.35;
    @SerialEntry public double groundFogSizeMin = 0.6;
    @SerialEntry public double groundFogSizeMax = 1.7;
    @SerialEntry public double groundFogWidthScale = 3.5;
    @SerialEntry public int groundFogLifetimeMin = 300;
    @SerialEntry public int groundFogLifetimeMax = 500;

    // fog distances
    @SerialEntry public double fogNear = 5.0;
    @SerialEntry public double fogFar = 25.0;
    @SerialEntry public double denseFogNear = 1.0;
    @SerialEntry public double denseFogFar = 8.0;
    @SerialEntry public double mistFogNear = 20.0;
    @SerialEntry public double mistFogFar  = 60.0;
    @SerialEntry public double hailFogNear = 15.0;
    @SerialEntry public double hailFogFar = 45.0;
    @SerialEntry public double blizzardFogNear = 5.0;
    @SerialEntry public double blizzardFogFar = 25.0;
    @SerialEntry public double freezingRainFogNear = 10.0;
    @SerialEntry public double freezingRainFogFar = 60.0;
    @SerialEntry public double thunderstormFogNear = 15.0;
    @SerialEntry public double thunderstormFogFar = 80.0;
    @SerialEntry public double sandstormFogNear = 5.0;
    @SerialEntry public double sandstormFogFar = 22.0;

    // fog colors
    @SerialEntry public int fogColor = 0xBFBFBF;
    @SerialEntry public int denseFogColor = 0xA6A6AD;
    @SerialEntry public int overcastColor = 0xA6A6AD;
    @SerialEntry public int mistColor = 0xC8CDD0;
    @SerialEntry public int thunderstormColor = 0x596173;
    @SerialEntry public int freezingRainColor = 0xB3CCE6;
    @SerialEntry public int hailColor = 0xCECFAB;
    @SerialEntry public int blizzardColor = 0xE0E0E0;
    @SerialEntry public int sandstormColor = 0xD9B05C;
    @SerialEntry public int sandstormBadlandsColor = 0xAA632B;


    public static AWClientConfig get() {
        return HANDLER.instance();
    }
    public void save() {
        HANDLER.save();
    }
    public static void init() {
        HANDLER.load();
    }
}