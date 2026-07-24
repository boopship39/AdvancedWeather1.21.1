package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.util.SkyMixinContext;
import net.antopfr.advancedweather.util.WeatherPalette;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class SkyColorMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void aw_overrideSkyColor(Vec3 cameraPos, float partialTick,
                                     CallbackInfoReturnable<Vec3> cir) {
        Vec3 vanilla = cir.getReturnValue();
        SkyMixinContext.setVanillaSkyColor(vanilla);

        Vec3 to   = WeatherPalette.tint(ClientWeatherState.getCurrentWeather());
        Vec3 from = WeatherPalette.tint(ClientWeatherState.getPreviousWeather());

        float t = ClientWeatherState.getSmoothedTransitionProgress(partialTick);
        Vec3 mix = from.lerp(to, t);

        Vec3 tinted = new Vec3(vanilla.x * mix.x, vanilla.y * mix.y, vanilla.z * mix.z);

        float ds = Mth.lerp(t, WeatherPalette.skyDesaturation(ClientWeatherState.getPreviousWeather()), WeatherPalette.skyDesaturation(ClientWeatherState.getCurrentWeather()));
        double lum = (tinted.x + tinted.y + tinted.z) / 3.0;
        cir.setReturnValue(new Vec3(
                Mth.lerp(ds, tinted.x, lum),
                Mth.lerp(ds, tinted.y, lum),
                Mth.lerp(ds, tinted.z, lum)));
    }
}
