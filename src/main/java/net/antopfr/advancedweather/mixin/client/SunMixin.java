package net.antopfr.advancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class SunMixin {

    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"
            )
    )
    private void aw_hideSunMoon(int unit, ResourceLocation texture) {
        WeatherTypes weather = ClientWeatherState.getCurrentWeather();

        boolean isSunOrMoon = texture.equals(
                ResourceLocation.withDefaultNamespace("textures/environment/sun.png")
        ) || texture.equals(
                ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png")
        );

        RenderSystem.setShaderTexture(unit, texture);

        if (isSunOrMoon) {
            WeatherTypes previous = ClientWeatherState.getPreviousWeather();
            float alphaCurrent  = advancedWeather1_21_1$isSunHidden(weather) ? 0.0f : 1.0f;
            float alphaPrevious = advancedWeather1_21_1$isSunHidden(previous) ? 0.0f : 1.0f;
            float alpha = alphaPrevious + (alphaCurrent - alphaPrevious) * ClientWeatherState.getSmoothedTransitionProgress(aw_partialTick);
            if (previous != weather) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            }
        } else {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Unique
    private float aw_partialTick = 1.0f;

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void aw_capturePartialTick(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        this.aw_partialTick = partialTick;
    }

    @Unique
    private static boolean advancedWeather1_21_1$isSunHidden(WeatherTypes type) {
        return switch (type) {
            case OVERCAST, HEAVY_RAIN, FREEZING_RAIN,
                 THUNDERSTORM, BLIZZARD, SNOW,
                 FOG, DENSE_FOG, LIGHT_RAIN -> true;
            default -> false;
        };
    }
}
