package net.antopfr.advancedweather.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public class StarRenderMixin {
//    @Redirect(
//            method = "drawStars",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/renderer/LevelRenderer;createStars(F)F"
//            )
//    )
//    private void onRenderStars(Tesselator tesselator, CallbackInfoReturnable<MeshData> cir) {
//        WeatherType type = ClientWeatherState.getCurrentWeather();
//        if (type == WeatherType.FOG || type == WeatherType.DENSE_FOG || type == WeatherType.BLIZZARD || type == WeatherType.SANDSTORM) {
//            cir.cancel();
//        }
//    }
}