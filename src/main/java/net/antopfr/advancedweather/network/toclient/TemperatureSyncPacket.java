package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TemperatureSyncPacket(float temperature, float forecast30)
        implements CustomPacketPayload {

    public static final Type<TemperatureSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("advancedweather", "temperature_sync"));

    public static final StreamCodec<FriendlyByteBuf, TemperatureSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, TemperatureSyncPacket::temperature,
            ByteBufCodecs.FLOAT, TemperatureSyncPacket::forecast30,
            TemperatureSyncPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TemperatureSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientAtmosphereState.updateTemperature(
                packet.temperature(),
                packet.forecast30()
        ));
    }
}
