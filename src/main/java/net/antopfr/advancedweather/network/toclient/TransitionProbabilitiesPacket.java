package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.client.state.ClientTransitionState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TransitionProbabilitiesPacket(List<Entry> entries) implements CustomPacketPayload {

    public record Entry(WeatherTypes type, float probabilityPercent) {}

    public static final Type<TransitionProbabilitiesPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("advancedweather", "transition_probabilities"));

    private static final StreamCodec<FriendlyByteBuf, Entry> ENTRY_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Entry decode(FriendlyByteBuf buf) {
            WeatherTypes type = WeatherTypes.values()[buf.readVarInt()];
            float prob = buf.readFloat();
            return new Entry(type, prob);
        }

        @Override
        public void encode(FriendlyByteBuf buf, Entry e) {
            buf.writeVarInt(e.type().ordinal());
            buf.writeFloat(e.probabilityPercent());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, TransitionProbabilitiesPacket> CODEC =
            ENTRY_CODEC.apply(ByteBufCodecs.list()).map(
                    TransitionProbabilitiesPacket::new,
                    TransitionProbabilitiesPacket::entries
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(TransitionProbabilitiesPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientTransitionState.updateProbabilities(packet.entries()));
    }
}
