package net.antopfr.advancedweather.content.item;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.item.almanac.WeatherAlmanacItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class AWItemProperties {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                AWItems.WEATHER_ALMANAC.get(),
                ResourceLocation.fromNamespaceAndPath("advancedweather", "filled"),
                (stack, level, entity, seed) -> {
                    int count = WeatherAlmanacItem.getRecords(stack).size();
                    int max = AWCommonConfig.get().almanacMaxRecords;
                    return count >= max ? 1.0f : 0.0f;
                }));
    }
}
