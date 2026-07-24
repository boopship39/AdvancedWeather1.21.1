package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.client.render.EndSkyHandler;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public class EndSkyMixin {

    @ModifyArg(
            method = "renderEndSky",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            index = 0
    )
    private int voidStormColor(int original) {
        if (ClientWeatherState.getCurrentWeather() != WeatherTypes.VOID_STORM) {
            return original;
        }
        int a = 0xFF;
        int r = (int) Mth.clamp(EndSkyHandler.red()   * 40f, 0, 255);
        int g = (int) Mth.clamp(EndSkyHandler.green() * 42f, 0, 255);
        int b = (int) Mth.clamp(EndSkyHandler.blue()  * 70f, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}