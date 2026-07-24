package net.antopfr.advancedweather.compat.sable;

import net.neoforged.bus.api.IEventBus;

public class SableIntegration {
    public static void init(IEventBus bus) {
        AWForceGroups.register(bus);
        ContraptionWindCompat.register();
    }
}
