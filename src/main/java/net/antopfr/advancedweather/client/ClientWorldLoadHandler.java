package net.antopfr.advancedweather.client;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID, value = Dist.CLIENT)
public class ClientWorldLoadHandler {

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) {
            ClientWeatherState.reset();
        } else {
            ClientWeatherState.resetForDimensionChange();
        }
    }
}
