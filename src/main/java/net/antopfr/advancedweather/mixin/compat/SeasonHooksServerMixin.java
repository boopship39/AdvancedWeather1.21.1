package net.antopfr.advancedweather.mixin.compat;

import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.season.SeasonHooks;

@Mixin(value = SeasonHooks.class, remap = false)
public class SeasonHooksServerMixin {

    @Inject(method = "getPrecipitationAtTickIceAndSnowHook", at = @At("RETURN"), cancellable = true, remap = false)
    private static void aw_overrideTickPrecipitation(LevelReader level, Biome biome, BlockPos pos,
                                                     CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        WeatherTypes type = WeatherManager.get(serverLevel).getCurrentWeather(serverLevel);

        if (type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        } else if (type == WeatherTypes.FREEZING_RAIN || type == WeatherTypes.LIGHT_RAIN
                || type == WeatherTypes.HEAVY_RAIN || type == WeatherTypes.DRIZZLE
                || type == WeatherTypes.THUNDERSTORM || type == WeatherTypes.HAIL) {
            cir.setReturnValue(Biome.Precipitation.RAIN);
        } else {
            cir.setReturnValue(Biome.Precipitation.NONE);
        }
    }

    @Inject(method = "coldEnoughToSnowSeasonal(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN"), cancellable = true, remap = false)
    private static void aw_overrideColdEnoughToSnow(LevelReader level, net.minecraft.core.Holder<Biome> biome, BlockPos pos,
                                                    CallbackInfoReturnable<Boolean> cir) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        WeatherTypes type = WeatherManager.get(serverLevel).getCurrentWeather(serverLevel);
        cir.setReturnValue(type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD || type == WeatherTypes.FREEZING_RAIN);
    }
}
