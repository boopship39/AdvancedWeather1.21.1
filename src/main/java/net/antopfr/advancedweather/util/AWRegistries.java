package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.weather.BiomeAtmosphere;
import net.antopfr.advancedweather.weather.BiomeFog;
import net.antopfr.advancedweather.weather.DimensionProfile;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID)
public class AWRegistries {

    public static final ResourceKey<Registry<DimensionProfile>> DIMENSION_PROFILE =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "dimension_profile"));

    public static final ResourceKey<Registry<BiomeAtmosphere>> BIOME_ATMOSPHERE =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "biome_atmosphere"));

    public static final ResourceKey<Registry<BiomeFog>> BIOME_FOG =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "biome_fog"));


    @SubscribeEvent
    public static void onNewDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(DIMENSION_PROFILE, DimensionProfile.CODEC, DimensionProfile.CODEC);
        event.dataPackRegistry(BIOME_ATMOSPHERE, BiomeAtmosphere.CODEC, BiomeAtmosphere.CODEC);
        event.dataPackRegistry(BIOME_FOG, BiomeFog.CODEC, BiomeFog.CODEC);
    }
}
