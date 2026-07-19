package net.antopfr.advancedweather.content.advancement;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.network.toserver.TempAchievementPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID, value = Dist.CLIENT)
public class TemperatureAchievement {
    private static int cooldown = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }
        cooldown = 20;

        float localTemp = ClientAtmosphereState.getLocalTemperature();

        if (localTemp <= -69.0f) {
            PacketDistributor.sendToServer(new TempAchievementPacket(false));
        }
        else if (localTemp >= 148.0f) {
            PacketDistributor.sendToServer(new TempAchievementPacket(true));
        }
    }
}
