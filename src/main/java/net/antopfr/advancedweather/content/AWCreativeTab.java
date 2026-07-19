package net.antopfr.advancedweather.content;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.util.AWRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class AWCreativeTab {
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TAB =
            AWRegistrate.get()
                    .generic("advancedweather", Registries.CREATIVE_MODE_TAB,
                            () -> CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.advancedweather"))
                                    .icon(AWBlocks.WEATHER_STATION::asStack)
                                    .build())
                    .register();

    public static void register() {}
}
