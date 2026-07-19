package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.client.ClientLocalHistory;
import net.antopfr.advancedweather.weather.WeatherHistory;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HistorySyncPacket(List<WeatherHistory.Entry> entries)
        implements CustomPacketPayload {

    public static final Type<HistorySyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("advancedweather", "history_sync"));

    private static final StreamCodec<FriendlyByteBuf, WeatherHistory.Entry> ENTRY_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull WeatherHistory.Entry decode(FriendlyByteBuf buf) {
            long gameTick = buf.readVarLong();
            float pressure = buf.readFloat();
            float wind = buf.readFloat();
            float temperature = buf.readFloat();
            float humidity = buf.readFloat();

            WeatherTypes weather = WeatherTypes.values()[buf.readVarInt()];
            WeatherTypes.Dimension dimension = WeatherTypes.Dimension.values()[buf.readVarInt()];

            return new WeatherHistory.Entry(gameTick, pressure, wind, temperature, humidity, weather, dimension);
        }

        @Override
        public void encode(FriendlyByteBuf buf, WeatherHistory.Entry e) {
            buf.writeVarLong(e.gameTick());
            buf.writeFloat(e.pressure());
            buf.writeFloat(e.wind());
            buf.writeFloat(e.temperature());
            buf.writeFloat(e.humidity());
            buf.writeVarInt(e.weather().ordinal());
            buf.writeVarInt(e.dimension().ordinal());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, HistorySyncPacket> CODEC =
            ENTRY_CODEC.apply(ByteBufCodecs.list()).map(
                    HistorySyncPacket::new,
                    HistorySyncPacket::entries
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(HistorySyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ClientLocalHistory.isEmpty()) {
                List<ClientLocalHistory.LocalEntry> convertedList =
                        packet.entries().stream().map(serverEntry ->
                                new ClientLocalHistory.LocalEntry(
                                        serverEntry.gameTick(),
                                        serverEntry.temperature(),
                                        serverEntry.humidity(),
                                        serverEntry.pressure(),
                                        serverEntry.wind(),
                                        serverEntry.weather()
                                )
                        ).toList();

                ClientLocalHistory.setEntries(convertedList);
            }
        });
    }
}
