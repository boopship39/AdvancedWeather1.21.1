package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WeatherSyncPacket(WeatherTypes weatherType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WeatherSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "weather_sync"));

    public static final StreamCodec<FriendlyByteBuf, WeatherSyncPacket> CODEC = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.map(WeatherTypes::fromNameSafe, Enum::name),
                    WeatherSyncPacket::weatherType,
                    WeatherSyncPacket::new
            );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WeatherSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientWeatherState.setCurrentWeather(packet.weatherType()));
    }
}
