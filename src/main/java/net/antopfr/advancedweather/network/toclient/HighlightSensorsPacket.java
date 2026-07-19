package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.block.sensor.SensorHighlighter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record HighlightSensorsPacket(List<BlockPos> validPositions,
                                     List<BlockPos> invalidPositions) implements CustomPacketPayload {

    public static final Type<HighlightSensorsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "highlight_sensors"));

    public static final StreamCodec<FriendlyByteBuf, HighlightSensorsPacket> CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeVarInt(pkt.validPositions.size());
                pkt.validPositions.forEach(buf::writeBlockPos);
                buf.writeVarInt(pkt.invalidPositions.size());
                pkt.invalidPositions.forEach(buf::writeBlockPos);
            },
            buf -> {
                int v = buf.readVarInt();
                List<BlockPos> valid = new ArrayList<>(v);
                for (int i = 0; i < v; i++) valid.add(buf.readBlockPos());
                int inv = buf.readVarInt();
                List<BlockPos> invalid = new ArrayList<>(inv);
                for (int i = 0; i < inv; i++) invalid.add(buf.readBlockPos());
                return new HighlightSensorsPacket(valid, invalid);
            });

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(HighlightSensorsPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() ->
                SensorHighlighter.show(pkt.validPositions(), pkt.invalidPositions()));
    }
}
