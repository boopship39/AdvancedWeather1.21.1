package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HumiditySyncPacket(float humidity, float forecast30)
        implements CustomPacketPayload {

    public static final Type<HumiditySyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("advancedweather", "humidity_sync"));

    public static final StreamCodec<FriendlyByteBuf, HumiditySyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, HumiditySyncPacket::humidity,
            ByteBufCodecs.FLOAT, HumiditySyncPacket::forecast30,
            HumiditySyncPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HumiditySyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientAtmosphereState.updateHumidity(
                packet.humidity(),
                packet.forecast30()
        ));
    }
}
