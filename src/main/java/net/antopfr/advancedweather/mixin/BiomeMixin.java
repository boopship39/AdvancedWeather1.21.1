package net.antopfr.advancedweather.mixin;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.antopfr.advancedweather.util.BiomeMixinContext;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Biome.class, priority = 1100)
public class BiomeMixin {

    @Inject(method = "getPrecipitationAt", at = @At("HEAD"), cancellable = true)
    private void onGetPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (cir.isCancelled()) return;

        ServerLevel level = BiomeMixinContext.getCurrentLevel();
        if (level == null) return;

        WeatherTypes type = WeatherManager.get(level).getCurrentWeather(level);
        if (type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        } else if (type == WeatherTypes.FREEZING_RAIN) {
            cir.setReturnValue(Biome.Precipitation.RAIN);
        }
    }
}
