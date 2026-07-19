package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class SkyColorMixin {

    @Inject(
            method = "getSkyColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void aw_overrideSkyColor(Vec3 cameraPos, float partialTick,
                                     CallbackInfoReturnable<Vec3> cir) {
        WeatherTypes weather = ClientWeatherState.getCurrentWeather();
        WeatherTypes previous = ClientWeatherState.getPreviousWeather();

        Vec3 overcastColor = advancedWeather1_21_1$getOvercastSkyColor(weather, (ClientLevel)(Object)this, 0);
        Vec3 previousColor = advancedWeather1_21_1$getOvercastSkyColor(previous, (ClientLevel)(Object)this, 0);

        if (overcastColor == null && previousColor == null) return;

        float t = ClientWeatherState.getSmoothedTransitionProgress(partialTick);

        Vec3 from = previousColor != null ? previousColor : cir.getReturnValue();
        Vec3 to   = overcastColor != null ? overcastColor : cir.getReturnValue();

        cir.setReturnValue(advancedWeather1_21_1$lerpVec3(from, to, t));
    }

    @Unique
    private static Vec3 advancedWeather1_21_1$getOvercastSkyColor(WeatherTypes type, ClientLevel level, float partialTick) {
        float brightness = level.getSkyDarken(partialTick);
        brightness = 0.3f + brightness * 0.6f;

        return switch (type) {
            case OVERCAST, HEAVY_RAIN, FREEZING_RAIN ->
                    new Vec3(0.65 * brightness, 0.65 * brightness, 0.68 * brightness);
            case THUNDERSTORM ->
                    new Vec3(0.28 * brightness, 0.30 * brightness, 0.35 * brightness);
            case BLIZZARD, SNOW ->
                    new Vec3(0.75 * brightness, 0.78 * brightness, 0.82 * brightness);
            case FOG, DENSE_FOG ->
                    new Vec3(0.72 * brightness, 0.72 * brightness, 0.74 * brightness);
            case DRIZZLE, LIGHT_RAIN ->
                    new Vec3(0.60 * brightness, 0.62 * brightness, 0.66 * brightness);
            case MIST ->
                    new Vec3(0.78 * brightness, 0.78 * brightness, 0.80 * brightness);
            default -> null;
        };
    }

    @Unique
    private static Vec3 advancedWeather1_21_1$lerpVec3(Vec3 a, Vec3 b, float t) {
        return new Vec3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }
}
