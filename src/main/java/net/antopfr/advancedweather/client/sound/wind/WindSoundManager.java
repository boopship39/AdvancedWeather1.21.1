package net.antopfr.advancedweather.client.sound.wind;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.client.sound.AWSounds;
import net.antopfr.advancedweather.weather.effect.global.wind.WindBurstManager;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class WindSoundManager {

    private static WindBurstSound lightWindInstance = null;
    private static WindBurstSound heavyWindInstance = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        WindBurstManager.tick();

        Player player = mc.player;

        if (ClientWeatherState.hasEffect(WeatherEffects.WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.NETHER_WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.END_WIND_LINES)) {

            if (lightWindInstance == null || !mc.getSoundManager().isActive(lightWindInstance)) {
                lightWindInstance = new WindBurstSound(player, AWSounds.WIND_LIGHT.get(), false);
                mc.getSoundManager().play(lightWindInstance);

            }

            if (heavyWindInstance == null || !mc.getSoundManager().isActive(heavyWindInstance)) {
                heavyWindInstance = new WindBurstSound(player, AWSounds.WIND_HEAVY.get(), true);
                mc.getSoundManager().play(heavyWindInstance);
            }

        } else {
            stopWindSounds(mc);
        }
    }

    public static void stopWindSounds(Minecraft mc) {
        if (lightWindInstance != null) {
            mc.getSoundManager().stop(lightWindInstance);
            lightWindInstance = null;
        }
        if (heavyWindInstance != null) {
            mc.getSoundManager().stop(heavyWindInstance);
            heavyWindInstance = null;
        }
    }
}
