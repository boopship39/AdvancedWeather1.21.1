package net.antopfr.advancedweather.network.toserver;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.block.autosampler.AutoSamplerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetSamplerIntervalPacket(BlockPos pos, int intervalTicks) implements CustomPacketPayload {
    public static final Type<SetSamplerIntervalPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "set_sampler_interval"));

    public static final StreamCodec<FriendlyByteBuf, SetSamplerIntervalPacket> CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeVarInt(pkt.intervalTicks); },
            buf -> new SetSamplerIntervalPacket(buf.readBlockPos(), buf.readVarInt()));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SetSamplerIntervalPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().level() instanceof ServerLevel level)) return;
            if (!pkt.pos().closerToCenterThan(ctx.player().position(), 8.0)) return;
            if (level.getBlockEntity(pkt.pos()) instanceof AutoSamplerBlockEntity sampler) {
                sampler.setInterval(pkt.intervalTicks());
            }
        });
    }
}