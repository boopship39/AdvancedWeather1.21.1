package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void onGetRainLevel(float delta, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientLevel)) return;

        float from = getRainIntensity(ClientWeatherState.getPreviousWeather());
        float to   = getRainIntensity(ClientWeatherState.getCurrentWeather());

        if (from < 0 && to < 0) return;

        float t        = ClientWeatherState.getSmoothedTransitionProgress(delta);
        float fromSafe = from < 0 ? 0f : from;
        float toSafe   = to   < 0 ? 0f : to;

        cir.setReturnValue(fromSafe + (toSafe - fromSafe) * t);
    }

    @Unique
    private static float getRainIntensity(WeatherTypes type) {
        return switch (type) {
            case DRIZZLE       -> 0.2f;
            case LIGHT_RAIN    -> 0.4f;
            case HEAVY_RAIN    -> 1.5f;
            case FREEZING_RAIN -> 0.8f;
            case THUNDERSTORM  -> 1.7f;
            case SNOW          -> 0.5f;
            case BLIZZARD      -> 1.0f;
            case HAIL          -> 0.7f;
            default            -> -1f;
        };
    }
}
