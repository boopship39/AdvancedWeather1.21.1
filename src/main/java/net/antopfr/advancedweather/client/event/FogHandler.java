package net.antopfr.advancedweather.client.event;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class FogHandler {

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (mc.level.dimension().equals(Level.END)) {
            float renderDistance = event.getRenderer().getRenderDistance();
            event.setNearPlaneDistance(renderDistance);
            event.setFarPlaneDistance(renderDistance * 10f);
            event.setCanceled(true);
            return;
        }

        WeatherTypes prev = ClientWeatherState.getPreviousWeather();
        WeatherTypes current = ClientWeatherState.getCurrentWeather();

        float partialTick = (float) event.getPartialTick();
        float progress = ClientWeatherState.getSmoothedTransitionProgress(partialTick);
        float renderDistance = event.getRenderer().getRenderDistance();

        ClientWeatherState.fogDistanceLerp.update(prev, current, progress, renderDistance);

        if (!ClientWeatherState.fogDistanceLerp.shouldRenderCustomFog()) {
            return;
        }

        event.setNearPlaneDistance(ClientWeatherState.fogDistanceLerp.getCurrentNear());
        event.setFarPlaneDistance(ClientWeatherState.fogDistanceLerp.getCurrentFar());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (mc.level.dimension().equals(Level.END)) {
            return; // laisse vanilla gérer — mais voir étape 3 pour forcer encore plus
        }

        WeatherTypes current = ClientWeatherState.getCurrentWeather();
        if (!current.hasFog()) return;

        float brightness = mc.level.getSkyDarken((float) event.getPartialTick());
        brightness = 0.3f + brightness * 0.6f;
        applyFogColor(event, ClientWeatherState.fogColorLerp.getCurrentColor(), brightness);
    }

    private static void applyFogColor(ViewportEvent.ComputeFogColor event, int color, float brightness) {
        event.setRed(((color >> 16) & 0xFF) / 255.0f * brightness);
        event.setGreen(((color >> 8) & 0xFF) / 255.0f * brightness);
        event.setBlue((color & 0xFF) / 255.0f * brightness);
    }
}
