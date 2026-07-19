package net.antopfr.advancedweather.network.toserver;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetStationNamePacket(BlockPos pos, String name) implements CustomPacketPayload {

    public static final Type<SetStationNamePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "set_station_name"));

    public static final StreamCodec<FriendlyByteBuf, SetStationNamePacket> CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeBlockPos(pkt.pos);
                buf.writeUtf(pkt.name, 32);
            },
            buf -> new SetStationNamePacket(buf.readBlockPos(), buf.readUtf(32)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetStationNamePacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().level() instanceof ServerLevel level)) return;
            if (!pkt.pos().closerToCenterThan(ctx.player().position(), 8.0)) return;
            if (level.getBlockEntity(pkt.pos()) instanceof WeatherStationBlockEntity station) {
                station.setStationName(pkt.name());
            }
        });
    }
}
