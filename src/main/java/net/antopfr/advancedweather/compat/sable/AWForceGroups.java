package net.antopfr.advancedweather.compat.sable;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import net.antopfr.advancedweather.AdvancedWeather;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AWForceGroups {

    private static final DeferredRegister<ForceGroup> FORCE_GROUPS =
            DeferredRegister.create(ForceGroups.REGISTRY_KEY, AdvancedWeather.MOD_ID);

    public static final DeferredHolder<ForceGroup, ForceGroup> WIND =
            FORCE_GROUPS.register("wind", () -> new ForceGroup(
                    Component.translatable("force_group.advancedweather.wind"),
                    Component.translatable("force_group.advancedweather.wind.desc"),
                    0x7FD4FF,
                    true));

    public static void register(IEventBus modBus) {
        FORCE_GROUPS.register(modBus);
    }
}
