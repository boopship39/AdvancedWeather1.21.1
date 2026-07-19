package net.antopfr.advancedweather.weather.effect.types.fog_wisps;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.client.particle.AWParticles;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class FogParticleHandler {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        AWClientConfig config = AWClientConfig.get();

        if (!ClientWeatherState.hasEffect(WeatherEffects.FOG_PARTICLES)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        tickCounter++;
        if (tickCounter % config.fogWispTickInterval != 0) return;

        Player player = mc.player;
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        int count = config.fogWispCount;

        for (int i = 0; i < count; i++) {
            double ox = (mc.level.random.nextDouble() - 0.5) * config.fogWispSpread;
            double oy = 1.0 + (mc.level.random.nextDouble() - 0.5) * config.fogWispHeightSpread;
            double oz = (mc.level.random.nextDouble() - 0.5) * config.fogWispSpread;

            mc.level.addParticle(
                    AWParticles.FOG_WISP.get(),
                    px + ox, py + oy, pz + oz,
                    0, 0, 0
            );
        }
    }
}
