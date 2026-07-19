package net.antopfr.advancedweather.network.toserver;

import net.antopfr.advancedweather.AdvancedWeather;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TempAchievementPacket(boolean isHot) implements CustomPacketPayload {

    public static final Type<TempAchievementPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "temperature_achievement"));

    public static final StreamCodec<FriendlyByteBuf, TempAchievementPacket> CODEC = CustomPacketPayload.codec(
            TempAchievementPacket::write,
            TempAchievementPacket::new
    );

    private TempAchievementPacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isHot);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
