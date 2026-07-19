package net.antopfr.advancedweather.content.entity;

import net.antopfr.advancedweather.weather.effect.types.chorus_plants.ChorusPlantRenderer;
import net.antopfr.advancedweather.weather.effect.types.rainbows.RainbowEntityRenderer;
import net.antopfr.advancedweather.weather.effect.types.tumbleweeds.TumbleweedRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class AWEntityRenderers {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AWEntities.TUMBLEWEED.get(),
                TumbleweedRenderer::new);
        event.registerEntityRenderer(AWEntities.CHORUS_PLANT.get(),
                ChorusPlantRenderer::new);
        event.registerEntityRenderer(AWEntities.RAINBOW.get(),
                RainbowEntityRenderer::new);
    }
}
