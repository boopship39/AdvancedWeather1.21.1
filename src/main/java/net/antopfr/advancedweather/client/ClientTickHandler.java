package net.antopfr.advancedweather.client;

import net.antopfr.advancedweather.client.debug.WeatherDebugOverlay;
import net.antopfr.advancedweather.client.debug.WeatherHistoryDebug;
import net.antopfr.advancedweather.client.debug.WeatherTransitionDebug;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.util.AWKeys;
import net.antopfr.advancedweather.util.EndSkyState;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        ClientWeatherState.tickTransition();
        ClientWeatherState.tickBiomeCheck(mc);

        ClientLocalHistory.tick(mc.level.getGameTime());

        EndSkyState.tick();

        if (AWKeys.HISTORY_KEY.consumeClick() && WeatherDebugOverlay.isEnabled()) {
            mc.setScreen(new WeatherHistoryDebug());
        }

        if (AWKeys.TRANSITIONS_KEY.consumeClick() && WeatherDebugOverlay.isEnabled()) {
            mc.setScreen(new WeatherTransitionDebug());
        }
    }
}