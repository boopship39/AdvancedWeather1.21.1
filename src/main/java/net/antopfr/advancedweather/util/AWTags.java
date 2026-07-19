package net.antopfr.advancedweather.util;

import net.antopfr.advancedweather.AdvancedWeather;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AWTags {
    public static final TagKey<Block> CAULDRON_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "cauldron_heat_sources"));
}
