package net.antopfr.advancedweather.client.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AWSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "advancedweather");

    public static final DeferredHolder<SoundEvent, SoundEvent> WIND_LIGHT = SOUNDS.register("wind_light",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("advancedweather", "wind_light")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WIND_HEAVY = SOUNDS.register("wind_heavy",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("advancedweather", "wind_heavy")));

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
