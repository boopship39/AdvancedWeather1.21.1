package net.antopfr.advancedweather.content;

import com.mojang.serialization.Codec;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.kite.KiteColors;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class AWDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancedWeather.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeatherRecord>> WEATHER_RECORD =
            COMPONENTS.register("weather_record", () -> DataComponentType.<WeatherRecord>builder()
                    .persistent(WeatherRecord.CODEC)
                    .networkSynchronized(WeatherRecord.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<WeatherRecord>>> WEATHER_ALMANAC =
            COMPONENTS.register("weather_almanac", () -> DataComponentType.<List<WeatherRecord>>builder()
                    .persistent(WeatherRecord.CODEC.listOf())
                    .networkSynchronized(WeatherRecord.STREAM_CODEC.apply(ByteBufCodecs.list()))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> CALIBRATED =
            COMPONENTS.register("calibrated", () -> DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KiteColors>> KITE_COLORS =
            COMPONENTS.register("kite_colors", () -> DataComponentType.<KiteColors>builder()
                    .persistent(KiteColors.CODEC)
                    .networkSynchronized(KiteColors.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> KITE_DEPLOYED =
            COMPONENTS.register("kite_deployed", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }
}
