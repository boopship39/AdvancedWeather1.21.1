package net.antopfr.advancedweather.compat.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateRegistries;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.block.station.WeatherStationDisplaySource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class AWDisplaySources {

    public static void onRegisterEvent(RegisterEvent event) {
        CreateCompatInternal.register(event);
    }

    public static void associateWithBlock() {
        CreateCompatInternal.associate();
    }

    private static class CreateCompatInternal {
        private static final WeatherStationDisplaySource WEATHER_REPORT = new WeatherStationDisplaySource();

        private static void register(RegisterEvent event) {
            event.register(CreateRegistries.DISPLAY_SOURCE, helper ->
                    helper.register(
                            ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "weather_report"),
                            WEATHER_REPORT
                    )
            );
        }

        private static void associate() {
            DisplaySource.BY_BLOCK.add(AWBlocks.WEATHER_STATION.get(), WEATHER_REPORT);
        }
    }
}
