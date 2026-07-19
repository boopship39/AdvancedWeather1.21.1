package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Biome.class, priority = 1100)
public class BiomeClientMixin {

    @Inject(method = "getPrecipitationAt", at = @At("HEAD"), cancellable = true)
    private void onGetPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (cir.isCancelled()) return;

        WeatherTypes type = ClientWeatherState.getCurrentWeather();

        if (type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        } else if (type == WeatherTypes.DRIZZLE
                || type == WeatherTypes.LIGHT_RAIN
                || type == WeatherTypes.HEAVY_RAIN
                || type == WeatherTypes.FREEZING_RAIN
                || type == WeatherTypes.THUNDERSTORM
                || type == WeatherTypes.HAIL) {
            cir.setReturnValue(Biome.Precipitation.RAIN);
        }
    }
}