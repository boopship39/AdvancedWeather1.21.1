package net.antopfr.advancedweather.network.toclient;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public record EffectSyncPacket(Set<WeatherEffects> effects) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EffectSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "effect_sync")
            );

    public static final StreamCodec<FriendlyByteBuf, EffectSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(
                    list -> {
                        Set<WeatherEffects> set = EnumSet.noneOf(WeatherEffects.class);
                        list.forEach(s -> {
                            WeatherEffects e = WeatherEffects.fromNameSafe(s);
                            if (e != null) set.add(e);
                        });
                        return set;
                    },
                    set -> set.stream().map(WeatherEffects::name).toList()
            ),
            EffectSyncPacket::effects,
            EffectSyncPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(EffectSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            WeatherTypes.Dimension currentDim = getCurrentClientDimension(context);

            Set<WeatherEffects> filtered = packet.effects().stream()
                    .filter(e -> e.isAllowedIn(currentDim))
                    .collect(java.util.stream.Collectors.toCollection(
                            () -> EnumSet.noneOf(WeatherEffects.class)));

            ClientWeatherState.setActiveEffects(filtered);
        });
    }

    private static WeatherTypes.Dimension getCurrentClientDimension(IPayloadContext context) {
        if (context.player() == null || context.player().level() == null)
            return WeatherTypes.Dimension.OVERWORLD;
        var dim = context.player().level().dimension();
        if (dim.equals(Level.NETHER)) return WeatherTypes.Dimension.NETHER;
        if (dim.equals(Level.END))    return WeatherTypes.Dimension.END;
        return WeatherTypes.Dimension.OVERWORLD;
    }
}
