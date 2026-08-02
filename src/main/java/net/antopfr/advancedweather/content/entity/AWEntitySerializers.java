package net.antopfr.advancedweather.content.entity;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.kite.KiteColors;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AWEntitySerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AdvancedWeather.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<KiteColors>> KITE_COLORS_SERIALIZER =
            ENTITY_SERIALIZERS.register("kite_colors", () ->
                    EntityDataSerializer.forValueType(KiteColors.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        ENTITY_SERIALIZERS.register(modBus);
    }
}
