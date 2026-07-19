package net.antopfr.advancedweather.mixin.compat;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.season.SeasonHooks;

@Mixin(value = SeasonHooks.class, remap = false)
public class SeasonHooksClientMixin {

    @Inject(method = "getPrecipitationAtSeasonal", at = @At("RETURN"), cancellable = true, remap = false)
    private static void aw_overridePrecipitationSeasonal(Level level, Holder<Biome> biome, BlockPos pos,
                                                         CallbackInfoReturnable<Biome.Precipitation> cir) {
        WeatherTypes type = ClientWeatherState.getCurrentWeather();

        if (type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        } else if (type == WeatherTypes.DRIZZLE || type == WeatherTypes.LIGHT_RAIN
                || type == WeatherTypes.HEAVY_RAIN || type == WeatherTypes.FREEZING_RAIN
                || type == WeatherTypes.THUNDERSTORM || type == WeatherTypes.HAIL) {
            cir.setReturnValue(Biome.Precipitation.RAIN);
        } else {
            cir.setReturnValue(Biome.Precipitation.NONE);
        }
    }

    @Inject(method = "hasPrecipitationSeasonal", at = @At("RETURN"), cancellable = true, remap = false)
    private static void aw_overrideHasPrecipitation(Level level, Holder<Biome> biome,
                                                    CallbackInfoReturnable<Boolean> cir) {
        WeatherTypes type = ClientWeatherState.getCurrentWeather();
        cir.setReturnValue(type.isVanillaRaining()
                || type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD);
    }
}
