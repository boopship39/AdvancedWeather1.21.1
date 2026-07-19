package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.client.debug.WeatherDebugOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ToggleDebugPacket() implements CustomPacketPayload {

    public static final ToggleDebugPacket INSTANCE = new ToggleDebugPacket();

    public static final Type<ToggleDebugPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("advancedweather", "toggle_debug"));

    public static final StreamCodec<FriendlyByteBuf, ToggleDebugPacket> CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ToggleDebugPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(WeatherDebugOverlay::toggle);
    }
}
