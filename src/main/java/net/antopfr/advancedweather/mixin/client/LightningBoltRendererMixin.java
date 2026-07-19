package net.antopfr.advancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LightningBoltRenderer.class)
public class LightningBoltRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void aw_tintLightning(LightningBolt entity, float yaw, float partialTick,
                                  PoseStack poseStack, MultiBufferSource bufferSource,
                                  int packedLight, CallbackInfo ci) {
        if (!(entity.level() instanceof ClientLevel)) return;

        WeatherTypes currentWeather = ClientWeatherState.getCurrentWeather();

        if (currentWeather != WeatherTypes.THUNDERSTORM &&
                currentWeather != WeatherTypes.NETHERSTORM &&
                currentWeather != WeatherTypes.ENDERSTORM) {
            return;
        }

        Random rng = new Random(entity.getId());

        if (currentWeather == WeatherTypes.THUNDERSTORM) {
            if (rng.nextFloat() < 0.4f) return;
        }

        float r = 1.0f, g = 1.0f, b = 1.0f;
        float roll = rng.nextFloat();

        if (currentWeather == WeatherTypes.NETHERSTORM) {
            if (roll < 0.50f) {
                // Rouge sang / Feu
                r = 1.0f;
                g = 0.1f + rng.nextFloat() * 0.2f;
                b = 0.1f + rng.nextFloat() * 0.1f;
            } else if (roll < 0.85f) {
                // Orange vif de lave
                r = 1.0f;
                g = 0.5f + rng.nextFloat() * 0.2f;
                b = 0.0f;
            } else {
                // Violet sombre
                r = 0.5f + rng.nextFloat() * 0.2f;
                g = 0.0f;
                b = 0.7f + rng.nextFloat() * 0.3f;
            }
        }
        else if (currentWeather == WeatherTypes.ENDERSTORM) {
            if (roll < 0.60f) {
                // Magenta / Rose violet de l'Ender
                r = 0.9f + rng.nextFloat() * 0.1f;
                g = 0.1f;
                b = 0.9f + rng.nextFloat() * 0.1f;
            } else if (roll < 0.90f) {
                // Vert Étrange / Énergie du vide
                r = 0.1f;
                g = 0.9f + rng.nextFloat() * 0.1f;
                b = 0.4f + rng.nextFloat() * 0.2f;
            } else {
                // Violet électrique très profond
                r = 0.3f + rng.nextFloat() * 0.1f;
                g = 0.05f;
                b = 0.5f + rng.nextFloat() * 0.1f;
            }
        }
        else {
            if (roll < 0.40f) {
                // Blanc violet
                r = 0.85f + rng.nextFloat() * 0.15f;
                g = 0.75f + rng.nextFloat() * 0.15f;
                b = 1.0f;
            } else if (roll < 0.65f) {
                // Blanc pur
                r = 1.0f;
                g = 0.95f + rng.nextFloat() * 0.05f;
                b = 0.90f + rng.nextFloat() * 0.10f;
            } else if (roll < 0.82f) {
                // Bleu blanc
                r = 0.70f + rng.nextFloat() * 0.15f;
                g = 0.80f + rng.nextFloat() * 0.10f;
                b = 1.0f;
            } else if (roll < 0.93f) {
                // Rouge électrique léger
                r = 0.9f;
                g = 0.65f + rng.nextFloat() * 0.20f;
                b = 0.45f + rng.nextFloat() * 0.20f;
            } else {
                // Vert électrique léger
                r = 0.60f + rng.nextFloat() * 0.20f;
                g = 0.9f;
                b = 0.60f + rng.nextFloat() * 0.20f;
            }
        }

        RenderSystem.setShaderColor(r, g, b, 1.0f);
    }
}