package net.antopfr.advancedweather.content.item.kite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KiteColors(int topRight, int topLeft, int bottomRight, int bottomLeft) {

    public static final KiteColors WHITE =
            new KiteColors(0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);

    public static final Codec<KiteColors> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("top_right").forGetter(KiteColors::topRight),
            Codec.INT.fieldOf("top_left").forGetter(KiteColors::topLeft),
            Codec.INT.fieldOf("bottom_right").forGetter(KiteColors::bottomRight),
            Codec.INT.fieldOf("bottom_left").forGetter(KiteColors::bottomLeft)
    ).apply(i, KiteColors::new));

    public static final StreamCodec<ByteBuf, KiteColors> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KiteColors::topRight,
            ByteBufCodecs.INT, KiteColors::topLeft,
            ByteBufCodecs.INT, KiteColors::bottomRight,
            ByteBufCodecs.INT, KiteColors::bottomLeft,
            KiteColors::new);
}
