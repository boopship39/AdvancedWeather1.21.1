package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.resources.ResourceLocation;

public class CloudUtil {
    private static final ResourceLocation AW_CLOUDS_CLOUDY =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/environment/clouds_cloudy.png");
    private static final ResourceLocation AW_CLOUDS_SCATTERED =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/environment/clouds_scattered.png");
    private static final ResourceLocation AW_CLOUDS_LIGHT =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/environment/clouds_light.png");
    private static final ResourceLocation AW_NO_CLOUDS =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/environment/no_clouds.png");
    public static final ResourceLocation VANILLA_CLOUDS =
            ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");

    public static ResourceLocation getCloudTexture() {
        WeatherTypes weather = ClientWeatherState.getCurrentWeather();
        return switch (weather) {
            case THUNDERSTORM, BLIZZARD, SNOW, HAIL, HEAVY_RAIN, OVERCAST, LIGHT_RAIN, DRIZZLE, CLOUDY  -> AW_CLOUDS_CLOUDY;
            case SUNNY                                                                         -> AW_CLOUDS_SCATTERED;
            case WINDY                                                                         -> AW_CLOUDS_LIGHT;
            case CLEAR                                                                         -> AW_NO_CLOUDS;
            default                                                                            -> VANILLA_CLOUDS;
        };
    }

    public static ResourceLocation getPreviousCloudTexture() {
        return switch (ClientWeatherState.getPreviousWeather()) {
            case THUNDERSTORM, BLIZZARD, SNOW, HAIL, HEAVY_RAIN, OVERCAST, LIGHT_RAIN, DRIZZLE, CLOUDY  -> AW_CLOUDS_CLOUDY;
            case SUNNY                                                                         -> AW_CLOUDS_SCATTERED;
            case WINDY                                                                         -> AW_CLOUDS_LIGHT;
            case CLEAR                                                                         -> AW_NO_CLOUDS;
            default                                                                            -> VANILLA_CLOUDS;
        };
    }

    public static boolean isTransitioning() {
        return !getCloudTexture().equals(getPreviousCloudTexture());
    }
}
