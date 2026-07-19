package net.antopfr.advancedweather.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class AWKeys {
    public static final String AW_CATEGORY = "key.categories.advancedweather";

    public static final KeyMapping HISTORY_KEY = new KeyMapping(
            "key.advancedweather.history",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            AW_CATEGORY
    );

    public static final KeyMapping TRANSITIONS_KEY = new KeyMapping(
            "key.advancedweather.transitions",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F9,
            AW_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(HISTORY_KEY);
        event.register(TRANSITIONS_KEY);
    }
}